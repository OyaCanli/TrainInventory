package com.canli.oya.traininventoryroom.ui.trains

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.UIState
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TrainViewModel (private val dataSource: ITrainDataSource,
                      private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var allItems: LiveData<PagedList<TrainMinimal>> = dataSource.getAllTrains()

    var listUiState : UIState = UIState(message = R.string.no_trains_found)

    fun getChosenTrain(trainId: Int) = dataSource.getChosenTrain(trainId)

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