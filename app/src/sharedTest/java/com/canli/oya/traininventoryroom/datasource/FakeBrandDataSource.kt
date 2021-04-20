package com.canli.oya.traininventoryroom.datasource

import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeBrandDataSource(private var brands : MutableList<BrandEntry> = sampleBrandList,
                                              private val trains: MutableList<TrainEntry> = mutableListOf()
)
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

    override suspend fun isThisItemUsed(item: BrandEntry): Int? {
        val index = trains.indexOfFirst { it.brandName == item.brandName }
        return if (index == -1) null else return 1
    }

    override suspend fun getItemNames(): List<String> {
        return brands.map { it.brandName }
    }

    override suspend fun isThisItemUsedInTrash(item: BrandEntry): Int? {
        val trainsInTrash = trains.filter {
            it.dateOfDeletion != null
        }
        //Search brand in trash folder
        val index = trainsInTrash.indexOfFirst { it.brandName == item.brandName }
        return if (index == -1) null else return 1
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: BrandEntry) {
        trains.removeAll {
            it.dateOfDeletion != null && it.brandName == item.brandName
        }
    }
}