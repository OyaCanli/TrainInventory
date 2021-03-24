package com.canli.oya.traininventoryroom.datasource

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTrainDataSource(private var trains: MutableList<TrainEntry> = sampleTrainList) :
    ITrainDataSource {

    private val trainsFlow: Flow<PagingData<TrainMinimal>> = flow {
        emit(PagingData.from(convertTrainsToMinimalTrains(trains)))
    }

    override fun getChosenTrain(trainId: Int): Flow<TrainEntry> {
        val index = trains.indexOfFirst { it.trainId == trainId }
        return flow {
            emit(trains[index])
        }
    }

    override suspend fun getAllTrainNames(): List<String> =
        trains.map { train -> train.trainName!! }

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

    override fun getTrainsFromThisBrand(brandName: String): Flow<PagingData<TrainMinimal>> {
        val filteredList = trains.filter { it.brandName == brandName }
        return flow {
            emit(PagingData.from(convertTrainsToMinimalTrains(filteredList)))
        }
    }

    override fun getTrainsFromThisCategory(category: String): Flow<PagingData<TrainMinimal>> {
        val filteredList = trains.filter { it.categoryName == category }
        return flow {
            emit(PagingData.from(convertTrainsToMinimalTrains(filteredList)))
        }
    }

    override fun searchInTrains(query: String): Flow<PagingData<TrainMinimal>> {
        val filteredList = trains.filter {
            it.trainName?.contains(query) == true || it.modelReference?.contains(query) == true
                    || it.description?.contains(query) == true
        }
        return flow {
            emit(PagingData.from(convertTrainsToMinimalTrains(filteredList)))
        }
    }

    override fun getAllTrains(): Flow<PagingData<TrainMinimal>> = trainsFlow

    private fun convertTrainsToMinimalTrains(trainsToConvert: List<TrainEntry>): List<TrainMinimal> {
        return trainsToConvert.map { it.convertToMinimal() }
    }

    fun setData(newTrainList: MutableList<TrainEntry>) {
        if (trains == newTrainList) return
        trains = newTrainList
    }
}

fun TrainEntry.convertToMinimal() =
    TrainMinimal(trainId, trainName, modelReference, brandName, categoryName, imageUri)

