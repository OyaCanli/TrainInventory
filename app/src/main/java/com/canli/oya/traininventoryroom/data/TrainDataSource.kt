package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import javax.inject.Inject

const val TRAINS_PAGE_SIZE = 15

class TrainDataSource @Inject constructor(private val database: TrainDatabase) {

    fun getAllTrains() : LiveData<PagedList<TrainMinimal>> {
        val factory = database.trainDao().allTrains
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

    fun getChosenTrainLiveData(trainId : Int) = database.trainDao().getChosenTrainLiveData(trainId)

    suspend fun insertTrain(train: TrainEntry) = database.trainDao().insert(train)

    suspend fun updateTrain(train: TrainEntry) = database.trainDao().update(train)

    suspend fun deleteTrain(train: TrainEntry) = database.trainDao().delete(train)

    suspend fun deleteTrain(trainId: Int) = database.trainDao().delete(trainId)

    fun getTrainsFromThisBrand(brandName: String): LiveData<PagedList<TrainMinimal>> {
        val factory = database.trainDao().getTrainsFromThisBrand(brandName)
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

    fun getTrainsFromThisCategory(category: String) : LiveData<PagedList<TrainMinimal>> {
        val factory = (database.trainDao().getTrainsFromThisCategory(category))
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

    fun searchInTrains(query: String): LiveData<PagedList<TrainMinimal>> {
        val factory = database.trainDao().searchInTrains(query)
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

}

