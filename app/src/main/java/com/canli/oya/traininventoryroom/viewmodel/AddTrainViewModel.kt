package com.canli.oya.traininventoryroom.viewmodel

import androidx.lifecycle.ViewModel
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class AddTrainViewModel (private val trainRepo: TrainRepository, val trainId: Int) : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var chosenTrain : TrainEntry? = null
    var trainBeingModified : TrainEntry? = null
        set(value) {
            /*Since we'll need to check for modifications and compare trainBeingModified
            to initial chosenTrain, we need to pass a copy of it. If we give a direct reference,
            they will seem to be the same instance and equality check always returns true.*/
            field = value?.copy()
        }

    private val emptyTrainObject: TrainEntry by lazy {
        TrainEntry().also { Timber.d("new instance of empty train") }
    }

    var isEdit: Boolean = false

    init {
        if(trainId > 0){ //Edit case
            isEdit = true
        } else { //Add case
            trainBeingModified = emptyTrainObject
            isEdit = false
        }
    }

    fun saveTrain() {
        if (!isEdit) {
            trainBeingModified?.let { insertTrain(it) }
        } else {
            trainBeingModified?.let { updateTrain(it) }
        }
    }

    private fun insertTrain(train: TrainEntry) {
        viewModelScope.launch { trainRepo.insertTrain(train) }
    }

    private fun updateTrain(train: TrainEntry) {
        viewModelScope.launch {trainRepo.updateTrain(train) }
    }

    var isChanged : Boolean = false
        get() = if(isEdit) trainBeingModified != chosenTrain
        else trainBeingModified != emptyTrainObject
        private set


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}