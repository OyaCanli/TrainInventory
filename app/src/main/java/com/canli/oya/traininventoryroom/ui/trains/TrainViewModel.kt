package com.canli.oya.traininventoryroom.ui.trains

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState
import com.canli.oya.traininventoryroom.common.provideTrainDataSource
import com.canli.oya.traininventoryroom.data.TrainDataSource
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrainViewModel(application: Application) : AndroidViewModel(application) {

    val context: Context = application.applicationContext

    private val dataSource: TrainDataSource = provideTrainDataSource(context)

    var trainListUiState: UIState = UIState(context.resources.getString(R.string.no_trains_found))

    var trainList = dataSource.getAllTrains()

    fun getChosenTrain(trainId: Int): Flowable<TrainEntry> = dataSource.getChosenTrainLiveData(trainId)

    fun deleteTrain(train: TrainEntry) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.deleteTrain(train) }
    }

    ///////////// SEARCH //////////////////////////
    fun getTrainsFromThisBrand(brandName: String) = dataSource.getTrainsFromThisBrand(brandName)

    fun getTrainsFromThisCategory(category: String) = dataSource.getTrainsFromThisCategory(category)

    suspend fun searchInTrains(query: String): List<TrainMinimal> {
        val searchResults = viewModelScope.async { dataSource.searchInTrains(query) }
        return searchResults.await()
    }
}