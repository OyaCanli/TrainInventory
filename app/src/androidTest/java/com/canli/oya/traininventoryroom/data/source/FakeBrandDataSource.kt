package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry

class FakeBrandDataSource(private var brands : MutableList<BrandEntry> = mutableListOf(),
                                              private val trains: List<TrainEntry> = listOf()) : IBrandCategoryDataSource<BrandEntry> {

    private val brandsLiveData : MutableLiveData<PagedList<BrandEntry>> = MutableLiveData()

    init {
        updateBrandsLiveData()
    }

    override suspend fun insertItem(brand: BrandEntry) {
        brands.add(brand)
        updateBrandsLiveData()
    }

    override suspend fun updateItem(brand: BrandEntry) {
        val index = brands.indexOfFirst { it.brandId == brand.brandId }
        brands[index] = brand
        updateBrandsLiveData()
    }

    override suspend fun deleteItem(brand: BrandEntry) {
        brands.remove(brand)
        updateBrandsLiveData()
    }

    override fun isThisItemUsed(brandName: String): Boolean {
        val index = trains.indexOfFirst { it.brandName == brandName }
        return (index != -1)
    }

    override fun getAllItems(): LiveData<PagedList<BrandEntry>> = brandsLiveData

    private fun updateBrandsLiveData(){
        brandsLiveData.value = brands.asPagedList()
    }

    fun setData(newBrandList : MutableList<BrandEntry>){
        brands = newBrandList
        updateBrandsLiveData()
    }
}