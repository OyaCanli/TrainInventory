package com.canli.oya.traininventoryroom.ui.brands

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource

import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.utils.getOrAwaitValue

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


class BrandViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var brandViewModel: BrandViewModel

    val sampleBrand1 = BrandEntry(0, "Markin")
    val sampleBrand2 = BrandEntry(1, "MDN")
    val sampleBrand3 = BrandEntry(2, "Legit")
    val sampleList = mutableListOf(sampleBrand1, sampleBrand2)

    @Before
    fun setupViewModel() {
        brandViewModel = BrandViewModel(
            FakeBrandDataSource(sampleList),
                Dispatchers.Unconfined)
    }

    @Test
    fun atLaunch_addItemChildFragIsNotVisible() {
        val value = brandViewModel.isChildFragVisible
        assertThat(value, CoreMatchers.`is`(false))
    }

    @Test
    fun getChosenBrand_returnsTheBrandSet() {
        brandViewModel.setChosenItem(sampleBrand1)
        val value = brandViewModel.chosenItem.getOrAwaitValue()
        assertThat(value, `is`(sampleBrand1))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertBrand_insertsTheBrand() {
        runBlockingTest {
            brandViewModel.insertItem(sampleBrand3)

            brandViewModel.allItems.collect { list ->
                //Verify that the list contains the new item
                assertTrue(list.contains(sampleBrand3))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteBrand_deletesTheBrand() {
        runBlockingTest {
            brandViewModel.deleteItem(sampleBrand2)

            brandViewModel.allItems.collect { list ->
                //Verify that the list doesn't contain that item anymore
                assertFalse(list.contains(sampleBrand2))
            }
        }
    }

}