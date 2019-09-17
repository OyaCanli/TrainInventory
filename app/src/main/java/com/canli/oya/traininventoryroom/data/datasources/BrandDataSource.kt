package com.canli.oya.traininventoryroom.data.datasources

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase

class BrandDataSource(val database: TrainDatabase) {

    fun getAllBrands() = database.brandDao().allBrands

    suspend fun getBrandList() = database.brandDao().getBrandList()

    suspend fun insertBrand(brand: BrandEntry) {
        database.brandDao().insertBrand(brand)
    }

    suspend fun updateBrand(brand: BrandEntry) {
        database.brandDao().updateBrandInfo(brand)
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        database.brandDao().deleteBrand(brand)
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return database.trainDao().isThisBrandUsed(brandName)
    }
}