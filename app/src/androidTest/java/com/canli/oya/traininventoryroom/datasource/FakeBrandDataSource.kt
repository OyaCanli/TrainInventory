package com.canli.oya.traininventoryroom.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeBrandDataSource(private var brands : MutableList<BrandEntry> = sampleBrandList,
                                              private val trains: List<TrainEntry> = listOf())
    : IBrandCategoryDataSource<BrandEntry> {

    private val brandsFlow : Flow<PagingData<BrandEntry>> = flow {
        emit(PagingData.from(brands))
    }

    override suspend fun insertItem(item: BrandEntry) {
        brands.add(item)
    }

    override suspend fun updateItem(item: BrandEntry) {
        val index = brands.indexOfFirst { it.brandId == item.brandId }
        brands[index] = item
    }

    override suspend fun deleteItem(item: BrandEntry) {
        brands.remove(item)
    }

    override fun getAllPagedItems(): Flow<PagingData<BrandEntry>> = brandsFlow

    override fun getAllItems(): Flow<List<BrandEntry>> = flow {
        emit(brands)
    }

    fun setData(newBrandList : MutableList<BrandEntry>){
        if(brands == newBrandList) return
        brands = newBrandList
    }

    override fun isThisItemUsed(item: BrandEntry): Boolean {
        val index = trains.indexOfFirst { it.brandName == item.brandName }
        return (index != -1)
    }
}