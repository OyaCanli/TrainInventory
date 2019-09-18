package com.canli.oya.traininventoryroom.data.dao

import androidx.room.*
import com.canli.oya.traininventoryroom.data.TrainEntry
import io.reactivex.Flowable

@Dao
interface TrainDao {

    @get:Query("SELECT * FROM trains")
    val allTrains: Flowable<List<TrainEntry>>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    fun getChosenTrainLiveData(id: Int): Flowable<TrainEntry>

    @Query("SELECT 1 FROM trains WHERE brandName = :brandName")
    fun isThisBrandUsed(brandName: String): Boolean

    @Query("SELECT 1 FROM trains WHERE categoryName = :categoryName")
    fun isThisCategoryUsed(categoryName: String): Boolean

    @Query("SELECT * FROM trains WHERE brandName = :brandName")
    fun getTrainsFromThisBrand(brandName: String): Flowable<List<TrainEntry>>

    @Query("SELECT * FROM trains WHERE categoryName = :categoryName")
    fun getTrainsFromThisCategory(categoryName: String): Flowable<List<TrainEntry>>

    @Query("SELECT * FROM trains WHERE (trainName LIKE '%' || :query || '%') " + "OR (modelReference LIKE '%' || :query || '%') OR (description LIKE '%' || :query || '%')")
    suspend fun searchInTrains(query: String): List<TrainEntry>

    @Insert
    suspend fun insertTrain(train: TrainEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrainInfo(train: TrainEntry)

    @Delete
    suspend fun deleteTrain(train: TrainEntry)
}
