package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import javax.inject.Inject

const val TRAINS_PAGE_SIZE = 15

class TrainDataSource @Inject constructor(private val database: TrainDatabase) : ITrainDataSource {

    override fun getAllTrains() : LiveData<PagedList<TrainMinimal>> {
        val factory = database.trainDao().observeAllTrains()
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

    override fun getChosenTrain(trainId : Int) = database.trainDao().observeChosenTrain(trainId)

    override suspend fun getAllTrainNames(): List<String> = database.trainDao().getAllTrainNames()

    override suspend fun insertTrain(train: TrainEntry) = database.trainDao().insert(train)

    override suspend fun updateTrain(train: TrainEntry) = database.trainDao().update(train)

    override suspend fun deleteTrain(train: TrainEntry) = database.trainDao().delete(train)

    override suspend fun deleteTrain(trainId: Int) = database.trainDao().delete(trainId)

    override fun getTrainsFromThisBrand(brandName: String): LiveData<PagedList<TrainMinimal>> {
        val factory = database.trainDao().observeTrainsFromThisBrand(brandName)
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

    override fun getTrainsFromThisCategory(category: String) : LiveData<PagedList<TrainMinimal>> {
        val factory = (database.trainDao().observeTrainsFromThisCategory(category))
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

    override fun searchInTrains(query: String): LiveData<PagedList<TrainMinimal>> {
        val factory = database.trainDao().searchInTrainsAndObserve(query)
        return LivePagedListBuilder(factory, TRAINS_PAGE_SIZE).build()
    }

}

