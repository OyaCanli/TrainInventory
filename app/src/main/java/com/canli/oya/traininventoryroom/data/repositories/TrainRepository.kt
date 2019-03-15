package com.canli.oya.traininventoryroom.data.repositories

import androidx.lifecycle.LiveData
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry

class TrainRepository private constructor(private val mDatabase: TrainDatabase) {

    val trainList: LiveData<List<TrainEntry>>

    init {
        trainList = loadTrains()
    }

    private fun loadTrains(): LiveData<List<TrainEntry>> {
        return mDatabase.trainDao().allTrains
    }

    fun getChosenTrainLiveData(trainId : Int): LiveData<TrainEntry> {
        return mDatabase.trainDao().getChosenTrainLiveData(trainId)
    }

    suspend fun getChosenTrain(trainId : Int): TrainEntry {
        return mDatabase.trainDao().getChosenTrain(trainId)
    }

    suspend fun insertTrain(train: TrainEntry) {
        mDatabase.trainDao().insertTrain(train)
    }

    suspend fun updateTrain(train: TrainEntry) {
        mDatabase.trainDao().updateTrainInfo(train)
    }

    suspend fun deleteTrain(train: TrainEntry) {
        mDatabase.trainDao().deleteTrain(train)
    }

    fun getTrainsFromThisBrand(brandName: String): LiveData<List<TrainEntry>> {
        return mDatabase.trainDao().getTrainsFromThisBrand(brandName)
    }

    fun getTrainsFromThisCategory(category: String): LiveData<List<TrainEntry>> {
        return mDatabase.trainDao().getTrainsFromThisCategory(category)
    }

    suspend fun searchInTrains(query: String): List<TrainEntry> {
        return mDatabase.trainDao().searchInTrains(query)
    }

    companion object {

        @Volatile private var sInstance: TrainRepository? = null

        fun getInstance(database: TrainDatabase): TrainRepository {
            return sInstance ?: synchronized(TrainRepository::class.java) {
                sInstance ?: TrainRepository(database).also { sInstance = it }
            }
        }
    }

}
