package com.canli.oya.traininventoryroom.ui.categories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CategoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var categoryViewModel: CategoryViewModel

    @Before
    fun setupViewModel() {
        categoryViewModel = CategoryViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun atLaunch_addItemChildFragIsNotVisible() {
        val value = categoryViewModel.isChildFragVisible.getOrAwaitValue()
        assertThat(value, `is`(false))
    }

    @Test
    fun atLaunch_defaultUIStateIsLoading() {
        val value = categoryViewModel.categoryListUiState.showLoading
        assertThat(value, `is`(true))
    }

    @Test
    fun getChosenCategory_returnsTheCategorySet() {
        val sampleCategory = CategoryEntry(0, "category")
        categoryViewModel.setChosenCategory(sampleCategory)
        val value = categoryViewModel.chosenCategory.getOrAwaitValue()
        assertThat(value, `is`(sampleCategory))
    }
}