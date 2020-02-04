package com.canli.oya.traininventoryroom.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal

@Dao
interface TrainDao : BaseDao<TrainEntry>{

    @get:Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains")
    val allTrains: DataSource.Factory<Int, TrainMinimal>

    @Query("SELECT trainName FROM trains")
    fun getAllTrainNames() : List<String>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    fun getChosenTrainLiveData(id: Int): LiveData<TrainEntry>

    @Query("SELECT 1 FROM trains WHERE brandName = :brandName")
    fun isThisBrandUsed(brandName: String): Boolean

    @Query("SELECT 1 FROM trains WHERE categoryName = :categoryName")
    fun isThisCategoryUsed(categoryName: String): Boolean

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE brandName = :brandName")
    fun getTrainsFromThisBrand(brandName: String): DataSource.Factory<Int, TrainMinimal>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE categoryName = :categoryName")
    fun getTrainsFromThisCategory(categoryName: String): DataSource.Factory<Int, TrainMinimal>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE (trainName LIKE '%' || :query || '%') " + "OR (modelReference LIKE '%' || :query || '%') OR (description LIKE '%' || :query || '%')")
    fun searchInTrains(query: String): DataSource.Factory<Int, TrainMinimal>

    @Query("DELETE FROM trains WHERE trainId = :trainId")
    suspend fun delete(trainId : Int)
}
