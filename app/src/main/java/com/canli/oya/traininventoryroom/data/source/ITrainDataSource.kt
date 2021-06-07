package com.canli.oya.traininventoryroom.data.source

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.data.entities.TrainEntity
import kotlinx.coroutines.flow.Flow

interface ITrainDataSource {

    fun getAllTrains() : Flow<PagingData<TrainMinimal>>

    fun getChosenTrain(trainId : Int): Flow<TrainEntity>

    suspend fun isThisTrainUsed(trainName : String) : Boolean

    suspend fun insertTrain(train: TrainEntity)

    suspend fun updateTrain(train: TrainEntity)

    suspend fun sendTrainToTrash(trainId: Int, dateOfDeletion : Long)

    suspend fun deleteTrainPermanently(trainId: Int)

    suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal>

    suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal>

    suspend fun searchInTrains(keyword: String?, category: String?, brand: String?): List<TrainMinimal>

    suspend fun getAllTrainsInTrash() : Flow<List<TrainMinimal>>

    suspend fun restoreTrainFromTrash(trainId : Int)
}