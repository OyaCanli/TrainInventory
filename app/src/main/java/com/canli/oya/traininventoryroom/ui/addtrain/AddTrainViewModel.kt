package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AddTrainViewModel(val trainInteractors: TrainInteractors,
                        brandInteractors: BrandCategoryInteractors<Brand>,
                        categoryInteractors: BrandCategoryInteractors<Category>,
                        private val chosenTrain: Train?,
                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : ViewModel() {

    val trainBeingModified = ObservableField<Train>()

    val brandList: Flow<List<Brand>> = brandInteractors.getAllItems()
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