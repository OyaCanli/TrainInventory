package com.canli.oya.traininventoryroom.ui.trains

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.datasource.provideTrainInteractor
import com.canli.oya.traininventoryroom.datasource.sampleTrain1
import com.canli.oya.traininventoryroom.datasource.sampleTrainList
import com.canli.oya.traininventoryroom.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TrainViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var trainViewModel: TrainViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var fakeTrainDataSource : FakeTrainDataSource

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Before
    fun setupViewModel() {
        fakeTrainDataSource = FakeTrainDataSource(sampleTrainList)
        trainViewModel = TrainViewModel(provideTrainInteractor(fakeTrainDataSource), Dispatchers.Unconfined)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChosenTrain_returnCorrectTrain() {
        runBlockingTest {
            val chosenTrain = trainViewModel.getChosenTrain(sampleTrain1.trainId).getOrAwaitValue()
            assertThat(chosenTrain?.trainName, `is`(sampleTrain1.trainName))
        }
    }

    //delete train with id deletes the train
    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrain_sendsItToTrash() {
        runBlockingTest {
            trainViewModel.sendTrainToTrash(sampleTrain1.trainId)
            val list = fakeTrainDataSource.getPlainTrainsForTesting()
            val deletedTrain = list.find { it.trainId == sampleTrain1.trainId }
            assertTrue(deletedTrain == null)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrainPermanently_deletesPermanently(){
        runBlockingTest {
            trainViewModel.deleteTrainPermanently(sampleTrain1.trainId)

            val list = fakeTrainDataSource.trains
            val index = list.indexOfFirst { it.trainName == sampleTrain1.trainName }
            assertTrue(index == -1)
        }
    }

}