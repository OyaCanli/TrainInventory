package com.canli.oya.traininventoryroom.ui.categories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.getOrAwaitValue
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    val sampleCategory1 = CategoryEntry(0, "Wagon")
    val sampleCategory2 = CategoryEntry(1, "Locomotive")
    val sampleCategory3 = CategoryEntry(2, "Accessoire")

    @Before
    fun setupViewModel() {
        val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2)
        categoryViewModel = CategoryViewModel(FakeCategoryDataSource(sampleCategoryList),
                Dispatchers.Unconfined
        )
    }

    @Test
    fun atLaunch_addItemChildFragIsNotVisible() {
        assertThat(categoryViewModel.isChildFragVisible, `is`(false))
    }

    @Test
    fun atLaunch_defaultUIStateIsLoading() {
        val value = categoryViewModel.listUiState.showLoading
        assertThat(value, `is`(true))
    }

    @Test
    fun getChosenCategory_returnsTheCategorySet() {
        val sampleCategory = CategoryEntry(0, "category")
        categoryViewModel.setChosenItem(sampleCategory)
        val value = categoryViewModel.chosenItem.getOrAwaitValue()
        assertThat(value, `is`(sampleCategory))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteCategory_deletesCategory() {
        runBlockingTest {
            categoryViewModel.deleteItem(sampleCategory2)
            val list = categoryViewModel.allItems.getOrAwaitValue().snapshot()
            //Verify that the list doesn't contain the item anymore
            assertFalse(list.contains(sampleCategory2))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertCategory_insertsCategory() {
        runBlockingTest {
            categoryViewModel.insertItem(sampleCategory3)
            
            val list = categoryViewModel.allItems.getOrAwaitValue().snapshot()
            //Verify that the list contains the new item
            assertTrue(list.contains(sampleCategory3))
        }
    }
}