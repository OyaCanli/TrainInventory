package com.canli.oya.traininventoryroom.data

class BrandDataSource(val database: TrainDatabase) {

    fun getAllBrands() = database.brandDao().allBrands

    suspend fun getBrandList() = database.brandDao().getBrandList()

    suspend fun insertBrand(brand: BrandEntry) {
        database.brandDao().insert(brand)
    }

    suspend fun updateBrand(brand: BrandEntry) {
        database.brandDao().update(brand)
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        database.brandDao().delete(brand)
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return database.trainDao().isThisBrandUsed(brandName)
    }
}