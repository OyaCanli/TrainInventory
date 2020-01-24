package com.canli.oya.traininventoryroom.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource

class FakeBrandDataSource(private val brands : MutableList<BrandEntry> = mutableListOf(),
                          private val trains: List<TrainEntry> = listOf()) : IBrandCategoryDataSource<BrandEntry> {

    private val brandsLiveData : MutableLiveData<PagedList<BrandEntry>> = MutableLiveData()

    init {
        updateBrandsLiveData()
    }

    override suspend fun insertItem(item: BrandEntry) {
        brands.add(item)
        updateBrandsLiveData()
    }

    override suspend fun updateItem(item: BrandEntry) {
        val index = brands.indexOfFirst { it.brandId == item.brandId }
        brands[index] = item
        updateBrandsLiveData()
    }

    override suspend fun deleteItem(item: BrandEntry) {
        brands.remove(item)
        updateBrandsLiveData()
    }

    override fun getAllItems(): LiveData<PagedList<BrandEntry>> = brandsLiveData

    private fun updateBrandsLiveData(){
        brandsLiveData.value = brands.asPagedList()
    }

    override fun isThisItemUsed(item: BrandEntry): Boolean {
        val index = trains.indexOfFirst { it.brandName == item.brandName }
        return (index != -1)
    }
}