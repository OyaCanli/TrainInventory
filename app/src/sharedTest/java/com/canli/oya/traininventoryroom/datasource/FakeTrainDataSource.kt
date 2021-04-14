package com.canli.oya.traininventoryroom.datasource

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTrainDataSource(var trains: MutableList<TrainEntry> = sampleTrainList) :
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

    override suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal> {
        val filteredList = trains.filter {
            it.brandName == brandName
        }
        return convertTrainsToMinimalTrains(filteredList)
    }

    override suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal> {
        val filteredList = trains.filter { it.categoryName == category }
        return convertTrainsToMinimalTrains(filteredList)
    }

    override suspend fun searchInTrains(
        keyword: String?,
        category: String?,
        brand: String?
    ): List<TrainMinimal> {
        val filteredList = ArrayList<TrainEntry>()
        filteredList.addAll(trains)
        if (!keyword.isNullOrBlank()) {
            val keywords = keyword.toLowerCase().split(" ")
            keywords.forEach { query ->
                filteredList.retainAll { train ->
                    train.trainName?.toLowerCase()?.contains(query) == true
                            || train.modelReference?.toLowerCase()?.contains(query) == true
                            || train.description?.toLowerCase()?.contains(query) == true
                }
            }
        }
        category?.let { category ->
            filteredList.retainAll { train ->
                train.categoryName == category
            }
        }

        brand?.let { brand ->
            filteredList.retainAll { train ->
                train.brandName == brand
            }
        }
        return convertTrainsToMinimalTrains(filteredList)
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

