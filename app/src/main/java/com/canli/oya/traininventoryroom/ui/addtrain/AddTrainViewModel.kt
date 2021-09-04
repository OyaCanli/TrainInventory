package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTrainViewModel @Inject constructor(val trainInteractors: TrainInteractors,
                                            brandInteractors: BrandCategoryInteractors<Brand>,
                                            categoryInteractors: BrandCategoryInteractors<Category>,
                                            savedStateHandle: SavedStateHandle,
                                            @IODispatcher private val ioDispatcher: CoroutineDispatcher)
    : ViewModel() {

    private val chosenTrain: Train? = savedStateHandle.get<Train>("chosenTrain")

    val trainBeingModified = ObservableField<Train>()

    val brandList = brandInteractors.getAllItems()
    val categoryList  = categoryInteractors.getAllItems()

    var isEdit: Boolean

    init {
        trainBeingModified.set(chosenTrain?.copy() ?: Train())
        isEdit = chosenTrain != null
    }

    fun saveTrain() {
        if (!isEdit) {
            trainBeingModified.get()?.let { insertTrain(it) }
        } else {
            trainBeingModified.get()?.let { updateTrain(it) }
        }
    }

    private fun insertTrain(train: Train) {
        viewModelScope.launch(ioDispatcher) { trainInteractors.addTrain(train) }
    }

    private fun updateTrain(train: Train) {
        viewModelScope.launch(ioDispatcher) { trainInteractors.updateTrain(train) }
    }

    suspend fun isThisTrainNameUsed(trainName : String) = trainInteractors.verifyUniquenessOfTrainName(trainName)

    var isChanged: Boolean = false
        get() = if (isEdit) trainBeingModified.get() != chosenTrain
        else trainBeingModified.get() != Train()
        private set
}