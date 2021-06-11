package com.canli.oya.traininventoryroom.ui.addtrain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.canli.oya.traininventoryroom.datasource.*
import com.canlioya.core.models.Train
import com.canlioya.core.usecases.brandcategory.*
import com.canlioya.core.usecases.trains.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


class AddTrainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

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
        assertEquals(sampleTrain1, addTrainViewModel.trainBeingModified.get())
    }

    @Test
    fun trainBeingModified_inAddMode_returnsEmptyTrain() {
        val addTrainViewModel = getViewModelForAddCase()
        assertEquals(Train(), addTrainViewModel.trainBeingModified.get())
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
        val changedSampleTrain = sampleTrain1.copy(trainName = "Blue Train")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    @Test
    fun isChanged_whenCategoryChangedInAddCase_returnsTrue() {
        val addTrainViewModel = getViewModelForAddCase()
        val changedSampleTrain = sampleTrain1.copy(categoryName = "Locomotive")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    @Test
    fun isChanged_whenNameChangedInEditCase_returnsTrue() {
        val addTrainViewModel = getViewModelForEditCase()
        val changedSampleTrain = Train(trainName = "New Train")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    //Save train in add mode inserts a new train
    @ExperimentalCoroutinesApi
    @Test
    fun saveTrain_inAddCase_insertsNewTrain() {
        runBlockingTest {
            val savedStateHandle = SavedStateHandle()
            val fakeTrainDataSource = FakeTrainDataSource(sampleTrainList)
            val initialSize = sampleTrainList.size
            val addTrainViewModel = AddTrainViewModel(provideTrainInteractor(fakeTrainDataSource),
                provideBrandInteractor(FakeBrandDataSource(sampleBrandList)), provideCategoryInteractor(FakeCategoryDataSource(sampleCategoryList)),
                savedStateHandle, Dispatchers.Unconfined)
            addTrainViewModel.saveTrain()
            val list = fakeTrainDataSource.trains
            assertThat(list.size, `is`(  initialSize + 1))
        }
    }
    //Save train in edit mode updates the train
    @ExperimentalCoroutinesApi
    @Test
    fun saveTrain_inEditMode_updatesTheTrain() {
        runBlockingTest {
            val args = mutableMapOf<String, Any>("chosenTrain" to sampleTrain1)
            val savedStateHandle = SavedStateHandle(args)
            val fakeTrainDataSource = FakeTrainDataSource(sampleTrainList)
            val initialSize = sampleTrainList.size
            val editViewModel = AddTrainViewModel(provideTrainInteractor(fakeTrainDataSource),
                provideBrandInteractor(FakeBrandDataSource(sampleBrandList)), provideCategoryInteractor(FakeCategoryDataSource(sampleCategoryList)),
                savedStateHandle, Dispatchers.Unconfined)
            editViewModel.saveTrain()
            val list = fakeTrainDataSource.trains
            assertThat(list.size, `is`(initialSize))
        }
    }


    private fun getViewModelForAddCase() : AddTrainViewModel {
        val savedStateHandle = SavedStateHandle()
        return AddTrainViewModel(provideTrainInteractor(FakeTrainDataSource(sampleTrainList)),
            provideBrandInteractor(FakeBrandDataSource(sampleBrandList)), provideCategoryInteractor(FakeCategoryDataSource(sampleCategoryList)),
            savedStateHandle, Dispatchers.Unconfined)
    }

    private fun getViewModelForEditCase() : AddTrainViewModel {
        val args = mutableMapOf<String, Any>("chosenTrain" to sampleTrain1)
        val savedStateHandle = SavedStateHandle(args)
        return AddTrainViewModel(provideTrainInteractor(FakeTrainDataSource(sampleTrainList)),
            provideBrandInteractor(FakeBrandDataSource(sampleBrandList)), provideCategoryInteractor(FakeCategoryDataSource(sampleCategoryList)),
            savedStateHandle, Dispatchers.Unconfined)
    }
}