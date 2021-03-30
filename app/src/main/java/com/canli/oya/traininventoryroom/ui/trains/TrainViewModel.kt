package com.canli.oya.traininventoryroom.ui.trains

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class TrainViewModel (val dataSource: ITrainDataSource,
                      private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var allItems: Flow<PagingData<TrainMinimal>> = dataSource.getAllTrains()

    fun getChosenTrain(trainId: Int) = liveData(ioDispatcher) {
        dataSource.getChosenTrain(trainId).collect {
            emit(it)
        }
    }

    fun deleteTrain(train: TrainEntry) {
        viewModelScope.launch(ioDispatcher) { dataSource.deleteTrain(train) }
    }

    fun deleteTrain(trainId: Int) {
        viewModelScope.launch(ioDispatcher) { dataSource.deleteTrain(trainId) }
    }

    ///////////// SEARCH //////////////////////////
    fun getTrainsFromThisBrand(brandName: String) = dataSource.getTrainsFromThisBrand(brandName)

    fun getTrainsFromThisCategory(category: String) = dataSource.getTrainsFromThisCategory(category)

    fun searchInTrains(query: String) = dataSource.searchInTrains(query)

}