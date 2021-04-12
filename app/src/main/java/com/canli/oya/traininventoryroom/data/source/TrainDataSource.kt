package com.canli.oya.traininventoryroom.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SupportSQLiteQuery
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val TRAINS_PAGE_SIZE = 15

class TrainDataSource @Inject constructor(private val database: TrainDatabase) : ITrainDataSource {

    override fun getAllTrains() : Flow<PagingData<TrainMinimal>> {
        val pager = Pager(config = PagingConfig(TRAINS_PAGE_SIZE, enablePlaceholders = true)) {
            database.trainDao().observeAllTrains()
        }
        return pager.flow
    }

    override fun getChosenTrain(trainId : Int) = database.trainDao().observeChosenTrain(trainId)

    override suspend fun getAllTrainNames(): List<String> = database.trainDao().getAllTrainNames()

    override suspend fun insertTrain(train: TrainEntry) = database.trainDao().insert(train)

    override suspend fun updateTrain(train: TrainEntry) = database.trainDao().update(train)

    override suspend fun deleteTrain(train: TrainEntry) = database.trainDao().delete(train)

    override suspend fun deleteTrain(trainId: Int) = database.trainDao().delete(trainId)

    override suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal> = database.trainDao().getTrainsFromThisBrand(brandName)

    override suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal> = database.trainDao().getTrainsFromThisCategory(category)

    override suspend fun searchInTrains(query: SupportSQLiteQuery): List<TrainMinimal> = database.trainDao().searchInTrains(query)


}

