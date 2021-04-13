package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
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

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains ORDER BY trainName")
    fun observeAllTrains(): PagingSource<Int, TrainMinimal>

    @Query("SELECT * FROM trains ORDER BY trainName")
    suspend fun getAllTrains() : List<TrainEntry>

    @Query("SELECT trainName FROM trains ORDER BY trainName")
    suspend fun getAllTrainNames() : List<String>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    fun observeChosenTrain(id: Int): Flow<TrainEntry>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    suspend fun getChosenTrain(id: Int): TrainEntry

    @Query("SELECT trainId FROM trains WHERE brandName = :brandName LIMIT 1")
    suspend fun isThisBrandUsed(brandName: String): Int?

    @Query("SELECT trainId FROM trains WHERE categoryName = :categoryName LIMIT 1")
    suspend fun isThisCategoryUsed(categoryName: String): Int?

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE brandName = :brandName ORDER BY trainName")
    suspend fun getTrainsFromThisBrand(brandName: String): List<TrainMinimal>

    @Query("SELECT * FROM trains WHERE brandName = :brandName")
    suspend fun getFullTrainsFromThisBrand(brandName: String): List<TrainEntry>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE categoryName = :categoryName ORDER BY trainName")
    suspend fun getTrainsFromThisCategory(categoryName: String): List<TrainMinimal>

    @Query("SELECT * FROM trains WHERE categoryName = :categoryName ORDER BY trainName")
    suspend fun getFullTrainsFromThisCategory(categoryName: String): List<TrainEntry>

    @RawQuery
    suspend fun searchInTrains(query: SupportSQLiteQuery): List<TrainMinimal>

    @Query("DELETE FROM trains WHERE trainId = :trainId")
    suspend fun delete(trainId : Int)
}
