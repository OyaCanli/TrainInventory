package com.canli.oya.traininventoryroom.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal
import io.reactivex.Flowable

@Dao
interface TrainDao : BaseDao<TrainEntry>{

    @get:Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains")
    val allTrains: Flowable<List<TrainMinimal>>

    @Query("SELECT * FROM trains WHERE trainId = :id")
    fun getChosenTrainLiveData(id: Int): Flowable<TrainEntry>

    @Query("SELECT 1 FROM trains WHERE brandName = :brandName")
    fun isThisBrandUsed(brandName: String): Boolean

    @Query("SELECT 1 FROM trains WHERE categoryName = :categoryName")
    fun isThisCategoryUsed(categoryName: String): Boolean

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE brandName = :brandName")
    fun getTrainsFromThisBrand(brandName: String): Flowable<List<TrainMinimal>>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE categoryName = :categoryName")
    fun getTrainsFromThisCategory(categoryName: String): Flowable<List<TrainMinimal>>

    @Query("SELECT trainId, trainName, modelReference, brandName, categoryName, imageUri FROM trains WHERE (trainName LIKE '%' || :query || '%') " + "OR (modelReference LIKE '%' || :query || '%') OR (description LIKE '%' || :query || '%')")
    suspend fun searchInTrains(query: String): List<TrainMinimal>

}
