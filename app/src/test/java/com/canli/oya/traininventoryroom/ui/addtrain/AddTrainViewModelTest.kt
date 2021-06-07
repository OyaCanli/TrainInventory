package com.canli.oya.traininventoryroom.ui.addtrain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.canli.oya.traininventoryroom.data.BrandEntity
import com.canli.oya.traininventoryroom.data.CategoryEntity
import com.canli.oya.traininventoryroom.data.TrainEntity
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource
import com.canli.oya.traininventoryroom.datasource.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
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

    private val sampleTrain = TrainEntity(trainId = 0, trainName = "Red Train", categoryName = "Vagon", brandName = "ModelTrains")
    val sampleTrain1 = TrainEntity(trainId = 0, trainName = "Red Wagon", categoryName = "Wagon", brandName = "Marklin")
    val sampleTrain2 = TrainEntity(trainId = 1, trainName = "Blue Loco", categoryName = "Locomotif", brandName = "MDN")
    val sampleTrain3 = TrainEntity(trainId = 2, trainName = "Gare", categoryName = "Accessoire", brandName = "Marklin")
    val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2, sampleTrain3)

    val sampleBrand1 = BrandEntity(0, "Markin")
    val sampleBrand2 = BrandEntity(1, "MDN")
    val sampleBrand3 = BrandEntity(2, "Legit")
    val sampleBrandList = mutableListOf(sampleBrand1, sampleBrand2, sampleBrand3)

    val sampleCategory1 = CategoryEntity(0, "Wagon")
    val sampleCategory2 = CategoryEntity(1, "Locomotive")
    val sampleCategory3 = CategoryEntity(2, "Accessoire")
    val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2, sampleCategory3)


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
        assertEquals(TrainEntity(), addTrainViewModel.trainBeingModified.get())
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
        val changedSampleTrain = TrainEntity(trainName = "New Train")
        addTrainViewModel.trainBeingModified.set(changedSampleTrain)
        assertThat(addTrainViewModel.isChanged, `is`(true))
    }

    //Save train in add mode inserts a new train
    @ExperimentalCoroutinesApi
    @Test
    fun saveTrain_inAddCase_insertsNewTrain() {
        runBlockingTest {
            val addTrainViewModel = getViewModelForAddCase()
            addTrainViewModel.saveTrain()
            val list = (addTrainViewModel.trainDataSource as FakeTrainDataSource).trains
            assertThat(list.size, `is`(4))
        }
    }
    //Save train in edit mode updates the train
    @ExperimentalCoroutinesApi
    @Test
    fun saveTrain_inEditMode_updatesTheTrain() {
        runBlockingTest {
            val addTrainViewModel = getViewModelForEditCase()
            addTrainViewModel.saveTrain()
            val list = (addTrainViewModel.trainDataSource as FakeTrainDataSource).trains
            assertThat(list.size, `is`(3))
        }
    }

    private fun getViewModelForAddCase() = AddTrainViewModel(FakeTrainDataSource(sampleTrainList), FakeBrandDataSource(sampleBrandList), FakeCategoryDataSource(sampleCategoryList), null, Dispatchers.Unconfined)

    private fun getViewModelForEditCase() = AddTrainViewModel(FakeTrainDataSource(sampleTrainList), FakeBrandDataSource(sampleBrandList), FakeCategoryDataSource(sampleCategoryList), sampleTrain, Dispatchers.Unconfined)

}