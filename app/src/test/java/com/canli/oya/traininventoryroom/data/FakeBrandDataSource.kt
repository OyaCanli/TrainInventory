package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.datasource.IBrandDataSource

class FakeBrandDataSource(private val brands : MutableList<BrandEntry> = mutableListOf(),
                          private val trains: List<TrainEntry> = listOf()) : IBrandDataSource {

    override suspend fun insertBrand(brand: BrandEntry) {
        brands.add(brand)
    }

    override suspend fun updateBrand(brand: BrandEntry) {
        val index = brands.indexOfFirst { it.brandId == brand.brandId }
        brands[index] = brand
    }

    override suspend fun deleteBrand(brand: BrandEntry) {
        brands.remove(brand)
    }

    override fun isThisBrandUsed(brandName: String): Boolean {
        val index = trains.indexOfFirst { it.brandName == brandName }
        return (index != -1)
    }

    override fun getAllBrands(): LiveData<PagedList<BrandEntry>> {
        return MutableLiveData() //TODO: not a real implementation
    }
}