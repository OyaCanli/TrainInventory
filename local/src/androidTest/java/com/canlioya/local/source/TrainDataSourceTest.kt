package com.canlioya.local.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canlioya.local.TrainDatabase
import com.canlioya.local.entities.toBrandEntity
import com.canlioya.local.entities.toCategoryEntity
import com.canlioya.local.entities.toTrainEntity
import com.canlioya.testresources.datasource.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TrainDataSourceTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TrainDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TrainDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun searchByBrand_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = com.canlioya.local.source.TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains(null, null, sampleBrand1.brandName)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain1.trainName)
    }

    @Test
    fun searchByBrand_whenThereIsNot_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = com.canlioya.local.source.TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains(null, null, sampleBrand3.brandName)
        assert(resultList.isEmpty())
    }

    @Test
    fun searchByCategory_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = com.canlioya.local.source.TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains(null, sampleCategory2.categoryName, null)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain2.trainName)
    }

    @Test
    fun searchByCategory_whenThereIsNot_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = com.canlioya.local.source.TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains(null, sampleCategory3.categoryName, null)
        assert(resultList.isEmpty())
    }

    @Test
    fun searchBySingleKeyword_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("blue", null, null)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain2.trainName)
    }

    @Test
    fun searchByMultipleKeywords_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("red mn", null, null)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain1.trainName)
    }

    @Test
    fun searchByMultipleKeywords_whenOnlyOneExists_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("red other", null, null)
        assert(resultList.isEmpty())
    }

    @Test
    fun searchByMultipleKeywords_existOnDifferentItems_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("red blue", null, null)
        assert(resultList.isEmpty())
    }

    @Test
    fun searchByBrandAndCategory_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains(null, sampleCategory1.categoryName, sampleBrand1.brandName)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain1.trainName)
    }

    @Test
    fun searchByBrandAndCategory_whenThereIsNot_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains(null, sampleCategory1.categoryName, sampleBrand2.brandName)
        assert(resultList.isEmpty())
    }

    @Test
    fun searchByBrandAndKeyword_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("mn", null, sampleBrand1.brandName)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain1.trainName)
    }

    @Test
    fun searchByBrandAndKeyword_whenThereIsNot_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("blue", null, sampleBrand1.brandName)
        assert(resultList.isEmpty())
    }

    @Test
    fun searchByCategoryAndKeyword_whenThereIs_givesCorrectResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("blue", sampleCategory2.categoryName, null)
        assert(resultList.size == 1)
        assert(resultList[0].trainName == sampleTrain2.trainName)
    }

    @Test
    fun searchByCategoryAndKeyword_whenThereIsNot_givesNoResult() = runBlockingTest {
        addSampleDataToDB()

        val trainDataSource = TrainDataSource(database)
        val resultList = trainDataSource.searchInTrains("blue", sampleCategory1.categoryName, null)
        assert(resultList.isEmpty())
    }

    private suspend fun addSampleDataToDB() {
        database.categoryDao().insert(sampleCategory1.toCategoryEntity())
        database.categoryDao().insert(sampleCategory2.toCategoryEntity())
        database.brandDao().insert(sampleBrand1.toBrandEntity())
        database.brandDao().insert(sampleBrand2.toBrandEntity())
        database.trainDao().insert(sampleTrain1.toTrainEntity())
        database.trainDao().insert(sampleTrain2.toTrainEntity())
    }
}