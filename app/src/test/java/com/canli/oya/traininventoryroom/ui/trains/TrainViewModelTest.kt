package com.canli.oya.traininventoryroom.ui.trains

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.canli.oya.traininventoryroom.data.TrainEntity
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TrainViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var trainViewModel: TrainViewModel

    val sampleTrain1 = TrainEntity(trainId = 0, trainName = "Red Wagon", categoryName = "Wagon", brandName = "Marklin")
    val sampleTrain2 = TrainEntity(trainId = 1, trainName = "Blue Loco", categoryName = "Locomotif", brandName = "MDN")
    val sampleTrain3 = TrainEntity(trainId = 2, trainName = "Gare", categoryName = "Accessoire", brandName = "Marklin")
    val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Before
    fun setupViewModel() {
        trainViewModel = TrainViewModel(FakeTrainDataSource(sampleTrainList), Dispatchers.Unconfined)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChosenTrain_returnCorrectTrain() {
        runBlockingTest {
            val chosenTrain = trainViewModel.getChosenTrain(sampleTrain2.trainId).getOrAwaitValue()
            assertTrue(chosenTrain.trainName == sampleTrain2.trainName)
        }
    }

    //delete train with id deletes the train
    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrain_sendsItToTrash() {
        runBlockingTest {
            trainViewModel.sendTrainToTrash(sampleTrain1.trainId)

            val list = (trainViewModel.dataSource as FakeTrainDataSource).trains
            val index = list.indexOfFirst { it.trainId == sampleTrain1.trainId }
            assertTrue(list[index].dateOfDeletion != null)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteTrainPermanently_deletesPermanently(){
        runBlockingTest {
            trainViewModel.deleteTrainPermanently(sampleTrain1.trainId)

            val list = (trainViewModel.dataSource as FakeTrainDataSource).trains
            val index = list.indexOfFirst { it.trainId == sampleTrain1.trainId }
            assertTrue(index == -1)
        }
    }

}