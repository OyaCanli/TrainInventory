package com.canlioya.local.daotests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canlioya.local.TrainDatabase
import com.canlioya.local.entities.BrandEntity
import com.canlioya.local.entities.CategoryEntity
import com.canlioya.local.entities.TrainEntity
import com.canlioya.local.entities.toTrain
import com.canlioya.testresources.datasource.convertToMinimal
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TrainDaoTests {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TrainDatabase

    private val firstCategory = CategoryEntity(5, "Wagon")
    private val secondCategory = CategoryEntity(6, "Locomotif")
    private val firstBrand = BrandEntity(
        brandId = 5,
        brandName = "Marklin",
        brandLogoUri = "first brand logo uri",
        webUrl = "https://www.google.com/"
    )
    private val secondBrand = BrandEntity(
        brandId = 6,
        brandName = "MDN",
        brandLogoUri = "second brand logo uri",
        webUrl = "https://github.com/"
    )

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
               TrainDatabase::class.java
        ).build()

        //Insert a few brands and categories
        runBlocking{
            database.categoryDao().insert(firstCategory)
            database.categoryDao().insert(secondCategory)
            database.brandDao().insert(firstBrand)
            database.brandDao().insert(secondBrand)
        }
    }

    @After
    fun closeDb() = database.close()

    //Insert a train, verify it is inserted //Shouldn't I add categories and brands before
    @Test
    fun insertTrain_verifyInserted() = runBlockingTest {
        //Then insert a train
        val trainToInsert = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        database.trainDao().insert(trainToInsert)

        //Get back the train
        val insertedTrain = database.trainDao().getChosenTrain(trainToInsert.trainId)

        //Verify it has expected values
        assertThat(insertedTrain as? TrainEntity, notNullValue())
        assertThat(insertedTrain.trainId, `is`(trainToInsert.trainId))
        assertThat(insertedTrain.trainName, `is`(trainToInsert.trainName))
        assertThat(insertedTrain.categoryName, `is`(trainToInsert.categoryName))
        assertThat(insertedTrain.brandName, `is`(trainToInsert.brandName))
        assertThat(insertedTrain.description, `is`(trainToInsert.description))
        assertThat(insertedTrain.imageUri, `is`(trainToInsert.imageUri))
        assertThat(insertedTrain.scale, `is`(trainToInsert.scale))
    }

    @Test
    fun updateTrain_verifyUpdated() = runBlockingTest {
        //Insert a train
        val trainToInsert = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        database.trainDao().insert(trainToInsert)

        //Then update the train
        trainToInsert.trainName = "Updated train"
        trainToInsert.brandName = secondBrand.brandName
        trainToInsert.categoryName = secondCategory.categoryName
        trainToInsert.description = "New description"
        database.trainDao().update(trainToInsert)

        //Get back the train
        val updatedTrain = database.trainDao().getChosenTrain(trainToInsert.trainId)

        //Verify it has updated values
        assertThat(updatedTrain.trainId, `is`(trainToInsert.trainId))
        assertThat(updatedTrain.trainName, `is`(trainToInsert.trainName))
        assertThat(updatedTrain.categoryName, `is`(trainToInsert.categoryName))
        assertThat(updatedTrain.brandName, `is`(trainToInsert.brandName))
        assertThat(updatedTrain.description, `is`(trainToInsert.description))
        assertThat(updatedTrain.imageUri, `is`(trainToInsert.imageUri))
        assertThat(updatedTrain.scale, `is`(trainToInsert.scale))
    }

    //Insert two trains than delete a train and verify deleted
    @Test
    fun deleteTrain_verifyDeleted() = runBlockingTest {
        //Insert two trains
        val firstTrain = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        val secondTrain = TrainEntity(
            trainId = 3,
            trainName = "Blue train",
            brandName = secondBrand.brandName,
            categoryName = secondCategory.categoryName,
            description = "second train description",
            imageUri = "second image url",
            scale = "3.5"
        )
        database.trainDao().insert(firstTrain)
        database.trainDao().insert(secondTrain)

        //Delete first train
        database.trainDao().deletePermanently(firstTrain.trainId)

        //Verify first train is deleted and the second train is still on database
        val allTrains = database.trainDao().getAllTrains()

        assertFalse(allTrains.contains(firstTrain))
        assertTrue(allTrains.contains(secondTrain))
    }

    //Insert two trains than recycle one train and verify it is no more in trains list
    //And that it is in trash list instead
    @Test
    fun recycleTrain_verifySendToTrash() = runBlockingTest {
        //Insert two trains
        val firstTrain = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        val secondTrain = TrainEntity(
            trainId = 3,
            trainName = "Blue train",
            brandName = secondBrand.brandName,
            categoryName = secondCategory.categoryName,
            description = "second train description",
            imageUri = "second image url",
            scale = "3.5"
        )
        database.trainDao().insert(firstTrain)
        database.trainDao().insert(secondTrain)

        //Delete first train
        val date = Date().time
        database.trainDao().sendToThrash(firstTrain.trainId, date)

        //Verify first train is not in the trains list anymore and the second train is still there
        val allTrains = database.trainDao().getAllTrains()
        assertFalse(allTrains.contains(firstTrain))
        assertTrue(allTrains.contains(secondTrain))

        //Verify that first train is in trash
        val trashedTrains = database.trainDao().getAllTrainsInTrash()
        assertTrue(trashedTrains.contains(firstTrain.toTrain().convertToMinimal()))
    }

    @Test
    fun isThisBrandUsed_forABrandUsed_returnsTrue() = runBlockingTest {
        //Insert a train
        val trainToInsert = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        database.trainDao().insert(trainToInsert)

        assertNotNull(database.trainDao().isThisBrandUsed(firstBrand.brandName))
    }

    @Test
    fun isThisBrandUsed_forABrandNotUsed_returnsFalse() = runBlockingTest {
        //Insert a train
        val trainToInsert = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        database.trainDao().insert(trainToInsert)

        assertNull(database.trainDao().isThisBrandUsed(secondBrand.brandName))
    }

    @Test
    fun isThisCategoryUsed_forACategoryUsed_returnsTrue() = runBlockingTest {
        //Insert a train
        val trainToInsert = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        database.trainDao().insert(trainToInsert)

        assertNotNull(database.trainDao().isThisCategoryUsed(firstCategory.categoryName))
    }

    @Test
    fun isThisCategoryUsed_forACategoryNotUsed_returnsFalse() = runBlockingTest {
        //Insert a train
        val trainToInsert = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        database.trainDao().insert(trainToInsert)
        val isThisCategoryUsed = database.trainDao().isThisCategoryUsed(secondCategory.categoryName)
        assertNull(isThisCategoryUsed)
        //assertFalse(isThisCategoryUsed)
    }

    @Test
    fun getTrainsFromThisBrand_returnsCorrectTrains() = runBlockingTest {
        //Insert three trains
        val firstTrain = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        val secondTrain = TrainEntity(
            trainId = 3,
            trainName = "Blue train",
            brandName = secondBrand.brandName,
            categoryName = secondCategory.categoryName,
            description = "second train description",
            imageUri = "second image url",
            scale = "3.5"
        )
        val thirdTrain = TrainEntity(
            trainId = 4,
            trainName = "Orange train",
            brandName = secondBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "third train description",
            imageUri = "third image url",
            scale = "5.2"
        )
        database.trainDao().insert(firstTrain)
        database.trainDao().insert(secondTrain)
        database.trainDao().insert(thirdTrain)

        val trainsFromSecondBrand = database.trainDao().getFullTrainsFromThisBrand(secondBrand.brandName)

        assertFalse(trainsFromSecondBrand.contains(firstTrain))
        assertTrue(trainsFromSecondBrand.contains(secondTrain))
        assertTrue(trainsFromSecondBrand.contains(thirdTrain))
    }

    @Test
    fun getTrainsFromThisCategory_returnsCorrectTrains() = runBlockingTest {
        //Insert three trains
        val firstTrain = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        val secondTrain = TrainEntity(
            trainId = 3,
            trainName = "Blue train",
            brandName = secondBrand.brandName,
            categoryName = secondCategory.categoryName,
            description = "second train description",
            imageUri = "second image url",
            scale = "3.5"
        )
        val thirdTrain = TrainEntity(
            trainId = 4,
            trainName = "Orange train",
            brandName = secondBrand.brandName,
            categoryName = firstCategory.categoryName,
            description = "third train description",
            imageUri = "third image url",
            scale = "5.2"
        )
        database.trainDao().insert(firstTrain)
        database.trainDao().insert(secondTrain)
        database.trainDao().insert(thirdTrain)

        val trainsFromFirstCategory = database.trainDao().getFullTrainsFromThisCategory(firstCategory.categoryName)

        assertTrue(trainsFromFirstCategory.contains(firstTrain))
        assertFalse(trainsFromFirstCategory.contains(secondTrain))
        assertTrue(trainsFromFirstCategory.contains(thirdTrain))
    }

    //Search in trains returns correct result
    @Test
    fun searchInTrains_returnsCorrectResults() = runBlockingTest {
        //Insert three trains
        val firstTrain = TrainEntity(
            trainId = 2,
            trainName = "Red train",
            brandName = firstBrand.brandName,
            categoryName = firstCategory.categoryName,
            modelReference = "Reference1",
            description = "train description",
            imageUri = "image url",
            scale = "1.3"
        )
        val secondTrain = TrainEntity(
            trainId = 3,
            trainName = "Blue train",
            brandName = secondBrand.brandName,
            categoryName = secondCategory.categoryName,
            modelReference = "Reference2",
            description = "second train description",
            imageUri = "second image url",
            scale = "3.5"
        )
        val thirdTrain = TrainEntity(
            trainId = 4,
            trainName = "Orange train",
            brandName = secondBrand.brandName,
            categoryName = firstCategory.categoryName,
            modelReference = "Reference3",
            description = "third train description",
            imageUri = "third image url",
            scale = "5.2"
        )
        database.trainDao().insert(firstTrain)
        database.trainDao().insert(secondTrain)
        database.trainDao().insert(thirdTrain)

        //Search number 1
        val firstSearchResult = database.trainDao().searchInTrains(SimpleSQLiteQuery(getSqlStatementForQuery("Blue")))
        assertTrue(firstSearchResult.contains(secondTrain.toTrain().convertToMinimal()))
        assertEquals(firstSearchResult.size, 1)

        //Verify search is NOT case sensitive
        val secondSearchResult = database.trainDao().searchInTrains(SimpleSQLiteQuery(getSqlStatementForQuery("ThiRd")))
        assertTrue(secondSearchResult.contains(thirdTrain.toTrain().convertToMinimal()))
        assertEquals(secondSearchResult.size, 1)

    }

    private fun getSqlStatementForQuery(keyword : String) : String {
        return "SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE " +
                "trainName LIKE '%$keyword%' OR modelReference LIKE '%$keyword%' OR description LIKE '%$keyword%'"
    }
}