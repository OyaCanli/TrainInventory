package com.canli.oya.traininventoryroom.viewmodel


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import io.reactivex.Flowable
import kotlinx.coroutines.launch

class AddTrainViewModel(private val trainRepo: TrainRepository,
        private val chosenTrain: TrainEntry?) : ViewModel() {

    val trainBeingModified = ObservableField<TrainEntry>()

    val brandList: Flowable<List<BrandEntry>> = trainRepo.getAllBrands()
    val categoryList: Flowable<List<String>> = trainRepo.getAllCategories()

    var isEdit: Boolean

    init {
        trainBeingModified.set(chosenTrain?.copy() ?: TrainEntry())
        isEdit = chosenTrain != null
    }

    fun saveTrain() {
        if (!isEdit) {
            trainBeingModified.get()?.let { insertTrain(it) }
        } else {
            trainBeingModified.get()?.let { updateTrain(it) }
        }
    }

    private fun insertTrain(train: TrainEntry) {
        viewModelScope.launch { trainRepo.insertTrain(train) }
    }

    private fun updateTrain(train: TrainEntry) {
        viewModelScope.launch { trainRepo.updateTrain(train) }
    }

    var isChanged: Boolean = false
        get() = if (isEdit) trainBeingModified.get() != chosenTrain
        else trainBeingModified.get() != TrainEntry()
        private set
}