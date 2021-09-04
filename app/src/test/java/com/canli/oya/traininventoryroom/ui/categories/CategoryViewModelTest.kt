package com.canli.oya.traininventoryroom.ui.categories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.canli.oya.traininventoryroom.provideCategoryInteractor
import com.canlioya.testresources.datasource.*
import com.canli.oya.traininventoryroom.utils.getOrAwaitValue
import com.canlioya.core.models.Category
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class CategoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var categoryViewModel: CategoryViewModel

    @Before
    fun setupViewModel() {
        categoryViewModel = CategoryViewModel(
            provideCategoryInteractor(FakeCategoryDataSource(sampleCategoryList)),
                Dispatchers.Unconfined
        )
    }

    @Test
    fun atLaunch_addItemChildFragIsNotVisible() {
        assertThat(categoryViewModel.isChildFragVisible, `is`(false))
    }

    @Test
    fun getChosenCategory_returnsTheCategorySet() {
        val sampleCategory = Category(0, "category")
        categoryViewModel.setChosenItem(sampleCategory)
        val value = categoryViewModel.chosenItem.getOrAwaitValue()
        assertThat(value, `is`(sampleCategory))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteCategory_deletesCategory() {
        runBlockingTest {
            categoryViewModel.deleteItem(sampleCategory2)
            categoryViewModel.allItems.collect { list ->
                //Verify that the list doesn't contain the item anymore
                assertFalse(list.contains(sampleCategory2))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertCategory_insertsCategory() {
        runBlockingTest {
            categoryViewModel.insertItem(sampleCategory3)
            categoryViewModel.allItems.collect { list ->
                //Verify that the list contains the new item
                assertTrue(list.contains(sampleCategory3))
            }
        }
    }
}