package com.canli.oya.traininventoryroom.ui.brands

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrandViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var brandViewModel: BrandViewModel

    @Before
    fun setupViewModel() {
        brandViewModel = BrandViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun atLaunch_addItemChildFragIsNotVisible() {
        val value = brandViewModel.isChildFragVisible.getOrAwaitValue()
        assertThat(value, CoreMatchers.`is`(false))
    }

    @Test
    fun atLaunch_defaultUIStateIsLoading() {
        val value = brandViewModel.brandListUiState.showLoading
        assertThat(value, CoreMatchers.`is`(true))
    }

    @Test
    fun getChosenCategory_returnsTheCategorySet() {
        val sampleBrand = BrandEntry(0, "brand")
        brandViewModel.setChosenBrand(sampleBrand)
        val value = brandViewModel.chosenBrand.getOrAwaitValue()
        assertThat(value, `is`(sampleBrand))
    }
}