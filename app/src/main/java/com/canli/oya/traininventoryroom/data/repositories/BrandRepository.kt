package com.canli.oya.traininventoryroom.data.repositories

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.datasources.BrandDataSource

class BrandRepository(private val brandDataSource: BrandDataSource) {

    fun getAllBrands() = brandDataSource.getAllBrands()

    suspend fun getBrandList() = brandDataSource.getBrandList()

    suspend fun insertBrand(brand: BrandEntry) {
        brandDataSource.insertBrand(brand)
    }

    suspend fun updateBrand(brand: BrandEntry) {
        brandDataSource.updateBrand(brand)
    }

    suspend fun deleteBrand(brand: BrandEntry) {
        brandDataSource.deleteBrand(brand)
    }

    fun isThisBrandUsed(brandName: String): Boolean {
        return brandDataSource.isThisBrandUsed(brandName)
    }
}
