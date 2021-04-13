package com.canli.oya.traininventoryroom.ui.trains

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


class TrainViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var trainViewModel: TrainViewModel

    val sampleTrain1 = TrainEntry(trainId = 0, trainName = "Red Wagon", categoryName = "Wagon", brandName = "Marklin")
    val sampleTrain2 = TrainEntry(trainId = 1, trainName = "Blue Loco", categoryName = "Locomotif", brandName = "MDN")
    val sampleTrain3 = TrainEntry(trainId = 2, trainName = "Gare", categoryName = "Accessoire", brandName = "Marklin")
    val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2)

    @Before
    fun setupViewModel() {
        trainViewModel = TrainViewModel(FakeTrainDataSource(sampleTrainList), Dispatchers.Unconfined)
    }

    //delete train with id deletes the train
    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrainWithId_deletesTheTrain() {
        runBlockingTest {
            trainViewModel.deleteTrain(sampleTrain1.trainId)

            val list = (trainViewModel.dataSource as FakeTrainDataSource).trains
            assertFalse(list.contains(sampleTrain1))
        }
    }

    //delete train with item deletes the train
    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrainEntry_deletesTheTrain() {
        runBlockingTest {
            trainViewModel.deleteTrain(sampleTrain1)

            val list = (trainViewModel.dataSource as FakeTrainDataSource).trains
            assertFalse(list.contains(sampleTrain1))
        }
    }

/*    //getTrainsFromThisBrand returns correct trains
    @ExperimentalCoroutinesApi
    @Test
    fun getTrainsFromThisBrand_returnsCorrectTrains() {
        runBlockingTest {
            val expectedValue = PagingData.from(mutableListOf(sampleTrain1.convertToMinimal()))
            trainViewModel.getTrainsFromThisBrand("Marklin").collect {
                assertTrue(it == expectedValue)
            }
        }
    }

    //getTrainsFromThisCategory returns correct trains
    @ExperimentalCoroutinesApi
    @Test
    fun getTrainsFromThisCategory_returnsCorrectTrains() {
        runBlockingTest {
            val expectedValue = PagingData.from(mutableListOf(sampleTrain2.convertToMinimal()))
            trainViewModel.getTrainsFromThisCategory("Locomotif").collect {
                assertTrue(it == expectedValue)
            }
        }
    }

    //Search in trains with valid query returns correct trains
    @ExperimentalCoroutinesApi
    @Test
    fun searchInTrains_returnsCorrectTrains() {
        runBlockingTest {
            val expectedValue = PagingData.from(mutableListOf(sampleTrain1.convertToMinimal()))
            trainViewModel.searchInTrains("Red").collect {
                assertTrue(it == expectedValue)
            }
        }
    }

    //Search in trains with empty query returns nothing
    @ExperimentalCoroutinesApi
    @Test
    fun searchInTrains_whenNoResult_returnsEmptyList() {
        runBlockingTest {
            val expectedValue = PagingData.from(mutableListOf())
            trainViewModel.searchInTrains("").collect {
                assertTrue(it == expectedValue)
            }
        }
    }*/

}