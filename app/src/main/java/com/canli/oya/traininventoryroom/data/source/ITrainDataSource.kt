package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingData
import androidx.sqlite.db.SupportSQLiteQuery
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import kotlinx.coroutines.flow.Flow

interface ITrainDataSource {

    fun getAllTrains() : Flow<PagingData<TrainMinimal>>

    fun getChosenTrain(trainId : Int): Flow<TrainEntry>

    suspend fun getAllTrainNames() : List<String>

    suspend fun insertTrain(train: TrainEntry)

    suspend fun updateTrain(train: TrainEntry)

    suspend fun deleteTrain(train: TrainEntry)

    suspend fun deleteTrain(trainId: Int)

    suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal>

    suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal>

    suspend fun searchInTrains(query: SupportSQLiteQuery): List<TrainMinimal>
}