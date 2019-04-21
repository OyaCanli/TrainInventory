package com.canli.oya.traininventoryroom.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.BrandRepository
import com.canli.oya.traininventoryroom.data.repositories.CategoryRepository
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import com.canli.oya.traininventoryroom.utils.provideBrandRepo
import com.canli.oya.traininventoryroom.utils.provideCategoryRepo
import com.canli.oya.traininventoryroom.utils.provideTrainRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class AddTrainViewModel(application: Application, val trainId: Int) : AndroidViewModel(application) {

    private val trainRepo: TrainRepository = provideTrainRepo(application)

    var chosenTrain: TrainEntry? = null
    val trainBeingModified = ObservableField<TrainEntry>()
    private val emptyTrainObject = TrainEntry()

    var brandList: List<BrandEntry>? = null
    var categoryList: List<String>? = null

    var isEdit: Boolean = false

    init {
        isEdit = trainId > 0
        if (isEdit) {
            viewModelScope.launch(Dispatchers.IO) {
                chosenTrain = trainRepo.getChosenTrain(trainId)
                trainBeingModified.set(chosenTrain?.copy())
            }
        } else {
            trainBeingModified.set(TrainEntry())
        }
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

    var isChanged : Boolean = false
        get() = if(isEdit) trainBeingModified.get() != chosenTrain
        else trainBeingModified.get() != emptyTrainObject
        private set

    var spinnerListener = AdapterViewBindingAdapter.OnItemSelected { spinner, _, position, _ ->
        //the listener is attached to both spinners.
        //when statement differentiate which spinners is selected
        when (spinner?.id) {
            R.id.brandSpinner -> {
                trainBeingModified.get()?.brandName = if(position == 0) null else brandList?.get(position-1)?.brandName
            }
            R.id.categorySpinner -> {
                trainBeingModified.get()?.categoryName = if(position == 0) null else categoryList?.get(position)
            }
        }
    }

    fun brandToPosition(brandName : String?): Int {
        //Set brand spinner
        val index = if(brandName == null) 0
                    else brandList?.indexOfFirst{it.brandName == brandName}?.plus(1) ?: 0
        Timber.d("index: $index, brandName : $brandName, brandlist size: ${brandList?.size}")
        return index
    }

    fun categoryToPosition(categoryName : String?) : Int {
        return if(categoryName == null) 0
                else categoryList?.indexOf(categoryName) ?: 0
    }


}