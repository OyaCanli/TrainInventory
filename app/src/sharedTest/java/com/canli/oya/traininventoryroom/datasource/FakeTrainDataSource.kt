package com.canli.oya.traininventoryroom.datasource

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.TrainEntity
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FakeTrainDataSource(var trains: MutableList<TrainEntity> = sampleTrainList) :
    ITrainDataSource {

    private val trainsFlow: Flow<PagingData<TrainMinimal>> = flow {
        val notDeletedTrains = trains.filter {
            it.dateOfDeletion == null
        }
        emit(PagingData.from(convertTrainsToMinimalTrains(notDeletedTrains)))
    }

    override fun getChosenTrain(trainId: Int): Flow<TrainEntity> {
        val index = trains.indexOfFirst { it.trainId == trainId }
        return flow {
            emit(trains[index])
        }
    }

    override suspend fun getAllTrainNames(): List<String> =
        trains
            .filter{it.dateOfDeletion == null}
            .map { train -> train.trainName!! }

    override suspend fun insertTrain(train: TrainEntity) {
        trains.add(train)
    }

    override suspend fun updateTrain(train: TrainEntity) {
        val index = trains.indexOfFirst { it.trainId == train.trainId }
        trains[index] = train
    }

    override suspend fun sendTrainToTrash(trainId: Int, dateOfDeletion: Long) {
        val index = trains.indexOfFirst { it.trainId == trainId }
        trains[index].dateOfDeletion = LocalDate.now().toEpochDay()
    }

    override suspend fun deleteTrainPermanently(trainId: Int) {
        val index = trains.indexOfFirst { it.trainId == trainId }
        trains.removeAt(index)
    }

    override suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal> {
        val filteredList = trains
            .filter { it.dateOfDeletion == null }
            .filter { it.brandName == brandName }
        return convertTrainsToMinimalTrains(filteredList)
    }

    override suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal> {
        val filteredList = trains
            .filter { it.dateOfDeletion == null }
            .filter { it.categoryName == category }
        return convertTrainsToMinimalTrains(filteredList)
    }

    override suspend fun searchInTrains(
        keyword: String?,
        category: String?,
        brand: String?
    ): List<TrainMinimal> {
        val filteredList = ArrayList<TrainEntity>()
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
        filteredList.retainAll {
            it.dateOfDeletion == null
            //TODO : might add a checkbox to search including trash
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

    override suspend fun getAllTrainsInTrash(): Flow<List<TrainMinimal>> = flow {
        val deletedTrains = trains.filter {
            it.dateOfDeletion != null
        }
        emit(convertTrainsToMinimalTrains(deletedTrains))
    }

    override suspend fun restoreTrainFromTrash(trainId: Int) {
        val index = trains.indexOfFirst { it.trainId == trainId }
        trains[index].dateOfDeletion = null
    }

    override fun getAllTrains(): Flow<PagingData<TrainMinimal>> = trainsFlow

    private fun convertTrainsToMinimalTrains(trainsToConvert: List<TrainEntity>): List<TrainMinimal> {
        return trainsToConvert.map { it.convertToMinimal() }
    }

    fun setData(newTrainList: MutableList<TrainEntity>) {
        if (trains == newTrainList) return
        trains = newTrainList
    }
}

fun TrainEntity.convertToMinimal() =
    TrainMinimal(trainId, trainName, modelReference, brandName, categoryName, imageUri)

