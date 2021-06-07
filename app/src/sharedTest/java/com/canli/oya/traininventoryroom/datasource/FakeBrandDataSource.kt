package com.canli.oya.traininventoryroom.datasource

import com.canli.oya.traininventoryroom.data.BrandEntity
import com.canli.oya.traininventoryroom.data.TrainEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeBrandDataSource(private var brands : MutableList<BrandEntity> = sampleBrandList,
                          private val trains: MutableList<TrainEntity> = mutableListOf()
)
    : IBrandCategoryDataSource<BrandEntity> {

    override suspend fun insertItem(item: BrandEntity) {
        brands.add(item)
    }

    override suspend fun updateItem(item: BrandEntity) {
        val index = brands.indexOfFirst { it.brandId == item.brandId }
        brands[index] = item
    }

    override suspend fun deleteItem(item: BrandEntity) {
        brands.remove(item)
    }

    override fun getAllItems(): Flow<List<BrandEntity>> = flow {
        emit(brands)
    }

    fun setData(newBrandList : MutableList<BrandEntity>){
        if(brands == newBrandList) return
        brands = newBrandList
    }

    override suspend fun isThisItemUsed(item: BrandEntity): Int? {
        val index = trains.indexOfFirst { it.brandName == item.brandName }
        return if (index == -1) null else return 1
    }

    override suspend fun getItemNames(): List<String> {
        return brands.map { it.brandName }
    }

    override suspend fun isThisItemUsedInTrash(item: BrandEntity): Int? {
        val trainsInTrash = trains.filter {
            it.dateOfDeletion != null
        }
        //Search brand in trash folder
        val index = trainsInTrash.indexOfFirst { it.brandName == item.brandName }
        return if (index == -1) null else return 1
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: BrandEntity) {
        trains.removeAll {
            it.dateOfDeletion != null && it.brandName == item.brandName
        }
    }
}