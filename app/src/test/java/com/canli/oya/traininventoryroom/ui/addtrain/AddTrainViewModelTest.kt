package com.canli.oya.traininventoryroom.ui.addtrain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.canli.oya.traininventoryroom.data.TrainEntry
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTrainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val sampleTrain = TrainEntry(trainId = 0, trainName = "Red Train", categoryName = "Vagon", brandName = "ModelTrains")

    @Test
    fun isEdit_withNullTrain_returnsFalse() {
        val addTrainViewModel = getViewModelForAddCase()
        assertThat(addTrainViewModel.isEdit, `is`(false))
    }

    @Test
    fun isEdit_withSomeTrain_returnsTrue() {
        val addTrainViewModel = getViewModelForEditCase()
        assertThat(addTrainViewModel.isEdit, `is`(true))
    }

    @Test
    fun trainBeingModified_inEditMode_returnsSampleTrain() {
        val addTrainViewModel = getViewModelForEditCase()
        assertEquals(sampleTrain, addTrainViewModel.trainBeingModified.get())
    }

    @Test
    fun trainBeingModified_inAddMode_returnsEmptyTrain() {
        val addTrainViewModel = getViewModelForAddCase()
        assertEquals(TrainEntry(), addTrainViewModel.trainBeingModified.get())
    }

    @Test
    fun isChanged_atLaunchInAddMode_isFalse() {
        val addTrainViewModel = getViewModelForAddCase()
        assertThat(addTrainViewModel.isChanged, `is`(false))
    }

    @Test
    fun isChanged_atLaunchInEditMode_isFalse() {
        val addTrainViewModel = getViewModelForEditCase()
        assertThat(addTrainViewModel.isChanged, `is`(false))
    }


    @Test
    fun isChanged_whenNameChangedInAddCase_returnsTrue() {
        val addTrainViewModel = getViewModelForAddCase()
        val changedSampleTrain = sampleTrain.copy(trainName = "Blue Train")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    @Test
    fun isChanged_whenCategoryChangedInAddCase_returnsTrue() {
        val addTrainViewModel = getViewModelForAddCase()
        val changedSampleTrain = sampleTrain.copy(categoryName = "Locomotive")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    @Test
    fun isChanged_whenNameChangedInEditCase_returnsTrue() {
        val addTrainViewModel = getViewModelForEditCase()
        val changedSampleTrain = TrainEntry(trainName = "New Train")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    private fun getViewModelForAddCase() = AddTrainViewModel(ApplicationProvider.getApplicationContext(), null)

    private fun getViewModelForEditCase() = AddTrainViewModel(ApplicationProvider.getApplicationContext(), sampleTrain)

}