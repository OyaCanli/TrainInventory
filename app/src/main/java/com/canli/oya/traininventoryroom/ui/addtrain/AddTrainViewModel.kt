package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.datasource.IBrandDataSource
import com.canli.oya.traininventoryroom.data.datasource.ICategoryDataSource
import com.canli.oya.traininventoryroom.data.datasource.ITrainDataSource
import kotlinx.coroutines.launch

class AddTrainViewModel(private val trainDataSource : ITrainDataSource,
                        brandDataSource: IBrandDataSource,
                        categoryDataSource : ICategoryDataSource,
                        private val chosenTrain: TrainEntry?) : ViewModel() {

    val trainBeingModified = ObservableField<TrainEntry>()

    val brandList = brandDataSource.getAllBrands()
    val categoryList  = categoryDataSource.getAllCategories()

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
        viewModelScope.launch { trainDataSource.insertTrain(train) }
    }

    private fun updateTrain(train: TrainEntry) {
        viewModelScope.launch { trainDataSource.updateTrain(train) }
    }

    var isChanged: Boolean = false
        get() = if (isEdit) trainBeingModified.get() != chosenTrain
        else trainBeingModified.get() != TrainEntry()
        private set
}