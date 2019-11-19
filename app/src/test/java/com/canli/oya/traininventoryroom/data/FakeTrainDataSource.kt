package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.datasource.ITrainDataSource

class FakeTrainDataSource(private val trains: MutableList<TrainEntry> = mutableListOf()) : ITrainDataSource {

    override fun getChosenTrain(trainId: Int): LiveData<TrainEntry> {
        val index = trains.indexOfFirst { it.trainId == trainId }
        val chosenTrainLiveData: MutableLiveData<TrainEntry> = MutableLiveData()
        chosenTrainLiveData.value = trains[index]
        return chosenTrainLiveData
    }

    override suspend fun insertTrain(train: TrainEntry) {
        trains.add(train)
    }

    override suspend fun updateTrain(train: TrainEntry) {
        val index = trains.indexOfFirst { it.trainId == train.trainId }
        trains[index] = train
    }

    override suspend fun deleteTrain(train: TrainEntry) {
        trains.remove(train)
    }

    override suspend fun deleteTrain(trainId: Int) {
        val index = trains.indexOfFirst { it.trainId == trainId }
        trains.removeAt(index)
    }

    override fun getTrainsFromThisBrand(brandName: String): LiveData<PagedList<TrainMinimal>> {
        return MutableLiveData() //TODO: not a real implementation
    }

    override fun getTrainsFromThisCategory(category: String): LiveData<PagedList<TrainMinimal>> {
        return MutableLiveData() //TODO: not a real implementation
    }

    override fun searchInTrains(query: String): LiveData<PagedList<TrainMinimal>> {
        return MutableLiveData() //TODO: not a real implementation
    }

    override fun getAllTrains(): LiveData<PagedList<TrainMinimal>> {
        return MutableLiveData() //TODO: not a real implementation
    }
}
