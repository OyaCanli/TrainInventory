package com.canli.oya.traininventoryroom.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import com.canli.oya.traininventoryroom.utils.provideTrainRepo
import kotlinx.coroutines.launch

class AddTrainViewModel(application: Application, private val chosenTrain: TrainEntry?) : AndroidViewModel(application) {

    private val trainRepo: TrainRepository = provideTrainRepo(application)

    val trainBeingModified = ObservableField<TrainEntry>()
    private val emptyTrainObject = TrainEntry()

    var brandList: List<BrandEntry>? = null
    var categoryList: List<String>? = null

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
        else trainBeingModified.get() != emptyTrainObject
        private set

    var spinnerListener = AdapterViewBindingAdapter.OnItemSelected { spinner, _, position, _ ->
        //the listener is attached to both spinners.
        //when statement differentiate which spinners is selected
        when (spinner?.id) {
            R.id.brandSpinner -> {
                trainBeingModified.get()?.brandName = if (position == 0) null else brandList?.get(position - 1)?.brandName
            }
            R.id.categorySpinner -> {
                trainBeingModified.get()?.categoryName = if (position == 0) null else categoryList?.get(position)
            }
        }
    }

}