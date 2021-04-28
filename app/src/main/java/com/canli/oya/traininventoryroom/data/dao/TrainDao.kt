package com.canli.oya.traininventoryroom.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainDao : BaseDao<TrainEntry>{

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE dateOfDeletion IS NULL ORDER BY trainName")
    fun observeAllTrains(): PagingSource<Int, TrainMinimal>

    @Query("SELECT * FROM trains WHERE dateOfDeletion IS NULL ORDER BY trainName")
    suspend fun getAllTrains() : List<TrainEntry>

    @Query("SELECT trainName FROM trains WHERE dateOfDeletion IS NULL ORDER BY trainName")
    suspend fun getAllTrainNames() : List<String>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    fun observeChosenTrain(id: Int): Flow<TrainEntry>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    suspend fun getChosenTrain(id: Int): TrainEntry

    @Query("SELECT trainId FROM trains WHERE dateOfDeletion IS NULL AND brandName = :brandName LIMIT 1")
    suspend fun isThisBrandUsed(brandName: String): Int?

    @Query("SELECT trainId FROM trains WHERE dateOfDeletion IS NOT NULL AND brandName = :brandName LIMIT 1")
    suspend fun isThisBrandUsedInTrash(brandName: String): Int?

    @Query("SELECT trainId FROM trains WHERE dateOfDeletion IS NULL AND categoryName = :categoryName LIMIT 1")
    suspend fun isThisCategoryUsed(categoryName: String): Int?

    @Query("SELECT trainId FROM trains WHERE dateOfDeletion IS NOT NULL AND categoryName = :categoryName LIMIT 1")
    suspend fun isThisCategoryUsedInTrash(categoryName: String): Int?

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE dateOfDeletion IS NULL AND brandName = :brandName ORDER BY trainName")
    suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal>

    @Query("SELECT * FROM trains WHERE dateOfDeletion IS NULL AND brandName = :brandName")
    suspend fun getFullTrainsFromThisBrand(brandName: String): List<TrainEntry>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE dateOfDeletion IS NULL AND categoryName = :categoryName ORDER BY trainName")
    suspend fun getTrainsFromThisCategory(categoryName: String): List<TrainMinimal>

    @Query("SELECT * FROM trains WHERE dateOfDeletion IS NULL AND categoryName = :categoryName ORDER BY trainName")
    suspend fun getFullTrainsFromThisCategory(categoryName: String): List<TrainEntry>

    @RawQuery
    suspend fun searchInTrains(query: SupportSQLiteQuery): List<TrainMinimal>

    @Query("DELETE FROM trains WHERE trainId = :trainId")
    suspend fun deletePermanently(trainId : Int)

    @Query("UPDATE trains SET dateOfDeletion = :dateOfDeletion WHERE trainId = :trainId")
    suspend fun sendToThrash(trainId: Int, dateOfDeletion: Long)

    @Query("UPDATE trains SET dateOfDeletion = NULL WHERE trainId = :trainId")
    suspend fun restoreFromThrash(trainId : Int)

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE dateOfDeletion IS NOT NULL")
    fun observeAllTrainsInTrash(): Flow<List<TrainMinimal>>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE dateOfDeletion IS NOT NULL")
    fun getAllTrainsInTrash(): List<TrainMinimal>

    @Query("DELETE FROM trains WHERE dateOfDeletion IS NOT NULL AND categoryName = :categoryName")
    fun deleteTrainsInTrashWithThisCategory(categoryName: String)

    @Query("DELETE FROM trains WHERE dateOfDeletion IS NOT NULL AND brandName = :brandName")
    fun deleteTrainsInTrashWithThisBrand(brandName: String)

    @Query("DELETE FROM trains WHERE dateOfDeletion IS NOT NULL AND dateOfDeletion < :date")
    fun cleanOldItemsInTrash(date: Long)
}
