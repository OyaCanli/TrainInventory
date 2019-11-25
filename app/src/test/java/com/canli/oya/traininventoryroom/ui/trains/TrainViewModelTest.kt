package com.canli.oya.traininventoryroom.ui.trains

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.FakeTrainDataSource
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.convertToMinimal
import com.canli.oya.traininventoryroom.di.TestTrainApplication
import com.canli.oya.traininventoryroom.getOrAwaitValue
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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
        val res = ApplicationProvider.getApplicationContext<TestTrainApplication>().resources
        trainViewModel = TrainViewModel(FakeTrainDataSource(sampleTrainList), res, Dispatchers.Unconfined)
    }

    @Test
    fun atLaunch_defaultUIStateIsLoading() {
        val value = trainViewModel.trainListUiState.showLoading
        assertThat(value, CoreMatchers.`is`(true))
    }

    //delete train with id deletes the train
    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrainWithId_deletesTheTrain() {
        runBlockingTest {
            trainViewModel.deleteTrain(sampleTrain1.trainId)

            val list: MutableList<TrainMinimal> = trainViewModel.trainList.getOrAwaitValue().snapshot()
            assertFalse(list.contains(sampleTrain1.convertToMinimal()))
        }
    }

    //delete train with item deletes the train
    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrainEntry_deletesTheTrain() {
        runBlockingTest {
            trainViewModel.deleteTrain(sampleTrain1)

            val list: MutableList<TrainMinimal> = trainViewModel.trainList.getOrAwaitValue().snapshot()
            assertFalse(list.contains(sampleTrain1.convertToMinimal()))
        }
    }

    //getTrainsFromThisBrand returns correct trains
    @ExperimentalCoroutinesApi
    @Test
    fun getTrainsFromThisBrand_returnsCorrectTrains() {
        runBlockingTest {
            val list = trainViewModel.getTrainsFromThisBrand("Marklin").getOrAwaitValue().snapshot()
            assertThat(list, IsEqual(mutableListOf(sampleTrain1.convertToMinimal())))
        }
    }

    //getTrainsFromThisCategory returns correct trains
    @ExperimentalCoroutinesApi
    @Test
    fun getTrainsFromThisCategory_returnsCorrectTrains() {
        runBlockingTest {
            val list = trainViewModel.getTrainsFromThisCategory("Locomotif").getOrAwaitValue().snapshot()
            assertThat(list, IsEqual(mutableListOf(sampleTrain2.convertToMinimal())))
        }
    }

    //Search in trains with valid query returns correct trains
    @ExperimentalCoroutinesApi
    @Test
    fun searchInTrains_returnsCorrectTrains() {
        runBlockingTest {
            val list = trainViewModel.searchInTrains("Red").getOrAwaitValue().snapshot()
            assertThat(list, IsEqual(mutableListOf(sampleTrain1.convertToMinimal())))
        }
    }

    //Search in trains with empty query returns nothing
    @ExperimentalCoroutinesApi
    @Test
    fun searchInTrains_withEmptyQuery_returnsCorrectTrains() {
        runBlockingTest {
            val list = trainViewModel.searchInTrains("").getOrAwaitValue().snapshot()
            assertThat(list, IsEqual(mutableListOf()))
        }
    }

}