package com.canlioya.core.data

import androidx.paging.PagingData
import com.canlioya.core.models.Train
import com.canlioya.core.models.TrainMinimal
import kotlinx.coroutines.flow.Flow

interface ITrainDataSource {

    fun getAllTrains(): Flow<PagingData<TrainMinimal>>

    fun getChosenTrain(trainId: Int): Flow<Train?>

    suspend fun isThisTrainNameUsed(trainName: String): Boolean

    suspend fun insertTrain(train: Train)

    suspend fun updateTrain(train: Train)

    suspend fun sendTrainToTrash(trainId: Int, dateOfDeletion: Long)

    suspend fun deleteTrainPermanently(trainId: Int)

    suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal>

    suspend fun getTrainsFromThisCategory(category: String): List<TrainMinimal>

    suspend fun searchInTrains(keyword: String?, category: String?, brand: String?): List<TrainMinimal>

    suspend fun getAllTrainsInTrash(): Flow<List<TrainMinimal>>

    suspend fun restoreTrainFromTrash(trainId: Int)
}
