package com.canli.oya.traininventoryroom.data.repositories

import androidx.lifecycle.LiveData

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.utils.AppExecutors

class TrainRepository private constructor(private val mDatabase: TrainDatabase, private val mExecutors: AppExecutors) {
    val trainList: LiveData<List<TrainEntry>>

    init {
        trainList = loadTrains()
    }

    private fun loadTrains(): LiveData<List<TrainEntry>> {
        return mDatabase.trainDao().allTrains
    }

    fun insertTrain(train: TrainEntry) {
        mExecutors.diskIO().execute { mDatabase.trainDao().insertTrain(train) }
    }

    fun updateTrain(train: TrainEntry) {
        mExecutors.diskIO().execute { mDatabase.trainDao().updateTrainInfo(train) }
    }

    fun deleteTrain(train: TrainEntry) {
        mExecutors.diskIO().execute { mDatabase.trainDao().deleteTrain(train) }
    }

    fun getTrainsFromThisBrand(brandName: String): LiveData<List<TrainEntry>> {
        return mDatabase.trainDao().getTrainsFromThisBrand(brandName)
    }

    fun getTrainsFromThisCategory(category: String): LiveData<List<TrainEntry>> {
        return mDatabase.trainDao().getTrainsFromThisCategory(category)
    }

    fun searchInTrains(query: String): List<TrainEntry> {
        return mDatabase.trainDao().searchInTrains(query)
    }

    companion object {

        private var sInstance: TrainRepository? = null

        fun getInstance(database: TrainDatabase, executors: AppExecutors): TrainRepository {
            return sInstance ?: synchronized(TrainRepository::class.java) {
                TrainRepository(database, executors).also { sInstance = it }
            }
        }
    }

}
