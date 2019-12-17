package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTrainViewModel(val trainDataSource : ITrainDataSource,
                        brandDataSource: IBrandCategoryDataSource<BrandEntry>,
                        categoryDataSource : IBrandCategoryDataSource<CategoryEntry>,
                        private val chosenTrain: TrainEntry?,
                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : ViewModel() {

    val trainBeingModified = ObservableField<TrainEntry>()

    val brandList = brandDataSource.getAllItems()
    val categoryList  = categoryDataSource.getAllItems()

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
        viewModelScope.launch(ioDispatcher) { trainDataSource.insertTrain(train) }
    }

    private fun updateTrain(train: TrainEntry) {
        viewModelScope.launch(ioDispatcher) { trainDataSource.updateTrain(train) }
    }

    var isChanged: Boolean = false
        get() = if (isEdit) trainBeingModified.get() != chosenTrain
        else trainBeingModified.get() != TrainEntry()
        private set
}