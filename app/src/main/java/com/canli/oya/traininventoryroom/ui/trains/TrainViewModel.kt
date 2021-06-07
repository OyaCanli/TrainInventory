package com.canli.oya.traininventoryroom.ui.trains

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.models.TrainMinimal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate


class TrainViewModel (private val interactors: TrainInteractors,
                      private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var allItems: Flow<PagingData<TrainMinimal>> = interactors.getAllTrains()

    fun getChosenTrain(trainId: Int) = liveData(ioDispatcher) {
        interactors.getChosenTrain(trainId).collectLatest {
            emit(it)
        }
    }

    fun sendTrainToTrash(trainId: Int) {
        val date = LocalDate.now().toEpochDay()
        viewModelScope.launch(ioDispatcher) { interactors.sendTrainToTrash(trainId, date) }
    }

    fun deleteTrainPermanently(trainId: Int){
        viewModelScope.launch(ioDispatcher) {
            interactors.deleteTrainPermanently(trainId)
        }
    }

    fun getTrainsInTrash() : LiveData<List<TrainMinimal>> = liveData {
        interactors.getAllTrainsInTrash().collectLatest {
            emit(it)
        }
    }

    suspend fun restoreTrain(trainId: Int) = interactors.restoreTrainFromTrash(trainId)

}