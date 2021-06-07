package com.canli.oya.traininventoryroom.ui.addtrain


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.data.entities.TrainEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AddTrainViewModel(val trainDataSource : ITrainDataSource,
                        brandDataSource: IBrandCategoryDataSource<BrandEntity>,
                        categoryDataSource : IBrandCategoryDataSource<CategoryEntity>,
                        private val chosenTrain: TrainEntity?,
                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : ViewModel() {

    val trainBeingModified = ObservableField<TrainEntity>()

    val brandList: Flow<List<BrandEntity>> = brandDataSource.getAllItems()
    val categoryList  = categoryDataSource.getAllItems()

    var isEdit: Boolean

    init {
        trainBeingModified.set(chosenTrain?.copy() ?: TrainEntity())
        isEdit = chosenTrain != null
    }

    fun saveTrain() {
        if (!isEdit) {
            trainBeingModified.get()?.let { insertTrain(it) }
        } else {
            trainBeingModified.get()?.let { updateTrain(it) }
        }
    }

    private fun insertTrain(train: TrainEntity) {
        viewModelScope.launch(ioDispatcher) { trainDataSource.insertTrain(train) }
    }

    private fun updateTrain(train: TrainEntity) {
        viewModelScope.launch(ioDispatcher) { trainDataSource.updateTrain(train) }
    }

    suspend fun isThisTrainNameUsed(trainName : String) = trainDataSource.isThisTrainUsed(trainName)

    var isChanged: Boolean = false
        get() = if (isEdit) trainBeingModified.get() != chosenTrain
        else trainBeingModified.get() != TrainEntity()
        private set
}