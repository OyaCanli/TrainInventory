package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal

class FakeTrainDataSource(private var trains: MutableList<TrainEntry> = sampleTrainList) : ITrainDataSource {

    private val trainsLiveData: MutableLiveData<PagedList<TrainMinimal>> = MutableLiveData()

    init {
        updateTrainsLiveData()
    }

    override fun getChosenTrain(trainId: Int): LiveData<TrainEntry> {
        val index = trains.indexOfFirst { it.trainId == trainId }
        val chosenTrainLiveData: MutableLiveData<TrainEntry> = MutableLiveData()
        chosenTrainLiveData.value = trains[index]
        return chosenTrainLiveData
    }

    override suspend fun getAllTrainNames(): List<String> = trains.map { train -> train.trainName!! }

    override suspend fun insertTrain(train: TrainEntry) {
        trains.add(train)
        updateTrainsLiveData()
    }

    override suspend fun updateTrain(train: TrainEntry) {
        val index = trains.indexOfFirst { it.trainId == train.trainId }
        trains[index] = train
        updateTrainsLiveData()
    }

    override suspend fun deleteTrain(train: TrainEntry) {
        trains.remove(train)
        updateTrainsLiveData()
    }

    override suspend fun deleteTrain(trainId: Int) {
        val index = trains.indexOfFirst { it.trainId == trainId }
        trains.removeAt(index)
        updateTrainsLiveData()
    }

    override fun getTrainsFromThisBrand(brandName: String): LiveData<PagedList<TrainMinimal>> {
        val filteredList = trains.filter { it.brandName == brandName }
        val resultLiveData: MutableLiveData<PagedList<TrainMinimal>> = MutableLiveData()
        resultLiveData.value = convertTrainsToMinimalTrains(filteredList).asPagedList()
        return resultLiveData
    }

    override fun getTrainsFromThisCategory(category: String): LiveData<PagedList<TrainMinimal>> {
        val filteredList = trains.filter { it.categoryName == category }
        val resultLiveData: MutableLiveData<PagedList<TrainMinimal>> = MutableLiveData()
        resultLiveData.value = convertTrainsToMinimalTrains(filteredList).asPagedList()
        return resultLiveData
    }

    override fun searchInTrains(query: String): LiveData<PagedList<TrainMinimal>> {
        val resultLiveData: MutableLiveData<PagedList<TrainMinimal>> = MutableLiveData()
        if(query.isBlank()){
            resultLiveData.value = emptyList<TrainMinimal>().asPagedList()
        } else {
            val filteredList = trains.filter { it.trainName?.contains(query)== true || it.modelReference?.contains(query) == true
                    || it.description?.contains(query) == true}
            resultLiveData.value = convertTrainsToMinimalTrains(filteredList).asPagedList()
        }
        return resultLiveData
    }

    override fun getAllTrains(): LiveData<PagedList<TrainMinimal>> = trainsLiveData

    private fun convertTrainsToMinimalTrains(trainsToConvert: List<TrainEntry>) : List<TrainMinimal> {
        return trainsToConvert.map {it.convertToMinimal() }
    }

    private fun updateTrainsLiveData() {
        trainsLiveData.value = convertTrainsToMinimalTrains(trains).asPagedList()
    }

    fun setData(newTrainList : MutableList<TrainEntry>){
        if(trains == newTrainList) return
        trains = newTrainList
        updateTrainsLiveData()
    }
}

fun TrainEntry.convertToMinimal() = TrainMinimal(trainId, trainName, modelReference, brandName, categoryName, imageUri)

