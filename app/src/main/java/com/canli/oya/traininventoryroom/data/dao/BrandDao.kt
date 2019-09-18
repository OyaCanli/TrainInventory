package com.canli.oya.traininventoryroom.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.canli.oya.traininventoryroom.data.BrandEntry
import io.reactivex.Flowable

@Dao
interface BrandDao : BaseDao<BrandEntry> {

    @get:Query("SELECT * FROM brands")
    val allBrands: Flowable<List<BrandEntry>>

    @Query("SELECT * FROM brands")
    suspend fun getBrandList() : List<BrandEntry>

    @Query("SELECT * FROM brands WHERE brandId = :id")
    fun getChosenBrand(id: Int): Flowable<BrandEntry>
}
