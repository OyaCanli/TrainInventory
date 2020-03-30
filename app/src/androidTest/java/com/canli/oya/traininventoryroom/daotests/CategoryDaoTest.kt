package com.canli.oya.traininventoryroom.daotests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

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
    fun insertCategory_verifyCategoryIsInserted() = runBlockingTest {
        //Insert a category
        val insertedCategory = CategoryEntry(5, "Wagon")
        database.categoryDao().insert(insertedCategory)

        //Get back the category
        val loadedCategory = database.categoryDao().getChosenCategory(insertedCategory.categoryId)

        //Verify it has expected values
        assertThat(loadedCategory as? CategoryEntry, notNullValue())
        assertThat(loadedCategory.categoryId, `is`(insertedCategory.categoryId))
        assertThat(loadedCategory.categoryName, `is`(insertedCategory.categoryName))
    }

    @Test
    fun updateCategory_verifyUpdated() = runBlockingTest {
        //Insert a category
        val insertedCategory = CategoryEntry(5, "Wagon")
        database.categoryDao().insert(insertedCategory)

        //Update the brand
        insertedCategory.categoryName = "updated name"
        database.categoryDao().update(insertedCategory)

        //Get back the category
        val loadedCategory = database.categoryDao().getChosenCategory(insertedCategory.categoryId)

        //Verify it has expected values
        assertThat(loadedCategory as? CategoryEntry, notNullValue())
        assertThat(loadedCategory.categoryId, `is`(insertedCategory.categoryId))
        assertThat(loadedCategory.categoryName, `is`(insertedCategory.categoryName))
    }

    @Test
    fun deleteCategory_verifyDeleted() = runBlockingTest {
        //Insert two categories
        val firstCategory = CategoryEntry(5, "Wagon")
        val secondCategory = CategoryEntry(6, "Locomotif")
        database.categoryDao().insert(firstCategory)
        database.categoryDao().insert(secondCategory)

        //Delete first category
        database.categoryDao().delete(firstCategory)

        //Verify that first category is deleted and second category is still on database
        val allCategories = database.categoryDao().getCategoryList()
        assertFalse(allCategories.contains(firstCategory))
        assertTrue(allCategories.contains(secondCategory))
    }
}