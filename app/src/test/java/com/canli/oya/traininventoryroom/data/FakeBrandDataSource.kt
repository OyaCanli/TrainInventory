package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.source.IBrandDataSource

class FakeBrandDataSource(private val brands : MutableList<BrandEntry> = mutableListOf(),
                          private val trains: List<TrainEntry> = listOf()) : IBrandDataSource {

    private val brandsLiveData : MutableLiveData<PagedList<BrandEntry>> = MutableLiveData()

    init {
        updateBrandsLiveData()
    }

    override suspend fun insertBrand(brand: BrandEntry) {
        brands.add(brand)
        updateBrandsLiveData()
    }

    override suspend fun updateBrand(brand: BrandEntry) {
        val index = brands.indexOfFirst { it.brandId == brand.brandId }
        brands[index] = brand
        updateBrandsLiveData()
    }

    override suspend fun deleteBrand(brand: BrandEntry) {
        brands.remove(brand)
        updateBrandsLiveData()
    }

    override fun isThisBrandUsed(brandName: String): Boolean {
        val index = trains.indexOfFirst { it.brandName == brandName }
        return (index != -1)
    }

    override fun getAllBrands(): LiveData<PagedList<BrandEntry>> = brandsLiveData

    private fun updateBrandsLiveData(){
        brandsLiveData.value = brands.asPagedList()
    }
}