package com.canli.oya.traininventoryroom.ui.trains

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate


class TrainViewModel (val dataSource: ITrainDataSource,
                      private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var allItems: Flow<PagingData<TrainMinimal>> = dataSource.getAllTrains()

    fun getChosenTrain(trainId: Int) = liveData(ioDispatcher) {
        dataSource.getChosenTrain(trainId).collect {
            emit(it)
        }
    }

    fun deleteTrain(trainId: Int) {
        val date = LocalDate.now().toEpochDay()
        viewModelScope.launch(ioDispatcher) { dataSource.deleteTrain(trainId, date) }
    }

    fun getTrainsInTrash() : LiveData<List<TrainMinimal>> = liveData {
        dataSource.getAllTrainsInTrash().collect {
            emit(it)
        }
    }


    suspend fun restoreTrain(trainId: Int) = dataSource.restoreTrainFromTrash(trainId)

}