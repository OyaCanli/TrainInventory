package com.canli.oya.traininventoryroom.data.datasources

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import io.reactivex.Flowable

class TrainDataSource(private val database: TrainDatabase) {

    fun getAllTrains()=  database.trainDao().allTrains

    fun getChosenTrainLiveData(trainId : Int) = database.trainDao().getChosenTrainLiveData(trainId)

    suspend fun insertTrain(train: TrainEntry) {
        database.trainDao().insert(train)
    }

    suspend fun updateTrain(train: TrainEntry) {
        database.trainDao().update(train)
    }

    suspend fun deleteTrain(train: TrainEntry) {
        database.trainDao().delete(train)
    }

    fun getTrainsFromThisBrand(brandName: String): Flowable<List<TrainEntry>> {
        return database.trainDao().getTrainsFromThisBrand(brandName)
    }

    fun getTrainsFromThisCategory(category: String): Flowable<List<TrainEntry>> {
        return database.trainDao().getTrainsFromThisCategory(category)
    }

    suspend fun searchInTrains(query: String): List<TrainEntry> {
        return database.trainDao().searchInTrains(query)
    }
}