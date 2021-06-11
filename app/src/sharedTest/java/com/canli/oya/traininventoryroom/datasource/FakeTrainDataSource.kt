package com.canli.oya.traininventoryroom.datasource

import androidx.paging.PagingData
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Train
import com.canlioya.core.models.TrainMinimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FakeTrainDataSource(var trains: MutableList<Train> = sampleTrainList) :
    ITrainDataSource {

    private val trainsFlow: Flow<PagingData<TrainMinimal>> = flow {
        val notDeletedTrains = trains.filter {
            it.dateOfDeletion == null
        }
        emit(PagingData.from(convertTrainsToMinimalTrains(notDeletedTrains)))
    }

    override fun getChosenTrain(trainId: Int): Flow<Train?> {
        val chosenTrain = trains.find { it.trainId == trainId }
        return flow {
            emit(chosenTrain)
        }
    }

    override suspend fun isThisTrainNameUsed(trainName: String): Boolean {
        val index = trains.indexOfFirst { it.trainName == trainName }
        return index != -1
    }

    override suspend fun insertTrain(train: Train) {
        trains.add(train)
    }

    override suspend fun updateTrain(train: Train) {
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
        val filteredList = ArrayList<Train>()
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

    fun getPlainTrainsForTesting(): List<TrainMinimal> {
        return trains.filter { it.dateOfDeletion == null }
            .map { it.convertToMinimal() }
    }


    private fun convertTrainsToMinimalTrains(trainsToConvert: List<Train>): List<TrainMinimal> {
        return trainsToConvert.map { it.convertToMinimal() }
    }

    fun setData(newTrainList: MutableList<Train>) {
        if (trains == newTrainList) return
        trains = newTrainList
    }
}

fun Train.convertToMinimal() =
    TrainMinimal(trainId, trainName, modelReference, brandName, categoryName, imageUri)

