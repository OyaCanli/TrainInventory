package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.BrandEntry

interface IBrandDataSource {
    fun getAllBrands() : LiveData<PagedList<BrandEntry>>

    suspend fun insertBrand(brand: BrandEntry)

    suspend fun updateBrand(brand: BrandEntry)

    suspend fun deleteBrand(brand: BrandEntry)

    fun isThisBrandUsed(brandName: String): Boolean
}