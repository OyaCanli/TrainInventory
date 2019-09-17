package com.canli.oya.traininventoryroom.data.datasources

import androidx.lifecycle.LiveData
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry

class TrainDataSource(private val database: TrainDatabase) {

    fun getAllTrains(): LiveData<List<TrainEntry>> {
        return database.trainDao().allTrains
    }

    fun getChosenTrainLiveData(trainId : Int): LiveData<TrainEntry> {
        return database.trainDao().getChosenTrainLiveData(trainId)
    }

    suspend fun insertTrain(train: TrainEntry) {
        database.trainDao().insertTrain(train)
    }

    suspend fun updateTrain(train: TrainEntry) {
        database.trainDao().updateTrainInfo(train)
    }

    suspend fun deleteTrain(train: TrainEntry) {
        database.trainDao().deleteTrain(train)
    }

    fun getTrainsFromThisBrand(brandName: String): LiveData<List<TrainEntry>> {
        return database.trainDao().getTrainsFromThisBrand(brandName)
    }

    fun getTrainsFromThisCategory(category: String): LiveData<List<TrainEntry>> {
        return database.trainDao().getTrainsFromThisCategory(category)
    }

    suspend fun searchInTrains(query: String): List<TrainEntry> {
        return database.trainDao().searchInTrains(query)
    }
}