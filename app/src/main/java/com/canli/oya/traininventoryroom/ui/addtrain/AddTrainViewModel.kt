package com.canli.oya.traininventoryroom.ui.addtrain


import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.common.provideBrandDataSource
import com.canli.oya.traininventoryroom.common.provideCategoryDataSource
import com.canli.oya.traininventoryroom.common.provideTrainDataSource
import com.canli.oya.traininventoryroom.data.TrainEntry
import kotlinx.coroutines.launch

class AddTrainViewModel(application: Application,
                        private val chosenTrain: TrainEntry?) : AndroidViewModel(application) {

    val context: Context = application.applicationContext

    private val trainDataSource = provideTrainDataSource(context)

    val trainBeingModified = ObservableField<TrainEntry>()

    val brandList = provideBrandDataSource(context).getAllBrands()
    val categoryList  = provideCategoryDataSource(context).getAllCategories()

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