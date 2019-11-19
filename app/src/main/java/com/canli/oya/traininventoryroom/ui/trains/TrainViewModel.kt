package com.canli.oya.traininventoryroom.ui.trains

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.datasource.ITrainDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TrainViewModel (private val dataSource: ITrainDataSource, resources : Resources) : ViewModel() {

    var trainListUiState: UIState = UIState(resources.getString(R.string.no_trains_found))

    var trainList = dataSource.getAllTrains()

    fun getChosenTrain(trainId: Int) = dataSource.getChosenTrain(trainId)

    fun deleteTrain(train: TrainEntry) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.deleteTrain(train) }
    }

    fun deleteTrain(trainId: Int) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.deleteTrain(trainId) }
    }

    ///////////// SEARCH //////////////////////////
    fun getTrainsFromThisBrand(brandName: String) = dataSource.getTrainsFromThisBrand(brandName)

    fun getTrainsFromThisCategory(category: String) = dataSource.getTrainsFromThisCategory(category)

    fun searchInTrains(query: String) = dataSource.searchInTrains(query)
}