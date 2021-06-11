package com.canli.oya.traininventoryroom.datasource


import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Train
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeBrandDataSource(private var brands : MutableList<Brand> = sampleBrandList,
                          private val trains: MutableList<Train> = mutableListOf()
)
    : IBrandCategoryDataSource<Brand> {

    override suspend fun insertItem(item: Brand) {
        brands.add(item)
    }

    override suspend fun updateItem(item: Brand) {
        val index = brands.indexOfFirst { it.brandId == item.brandId }
        brands[index] = item
    }

    override suspend fun deleteItem(item: Brand) {
        brands.remove(item)
    }

    override fun getAllItems(): Flow<List<Brand>> = flow {
        emit(brands)
    }

    fun setData(newBrandList : MutableList<Brand>){
        if(brands == newBrandList) return
        brands = newBrandList
    }

    override suspend fun isThisItemUsed(item: Brand): Boolean {
        val index = trains.indexOfFirst { it.brandName == item.brandName }
        return index != -1
    }

    override suspend fun getItemNames(): List<String> {
        return brands.map { it.brandName }
    }

    override suspend fun isThisItemUsedInTrash(item: Brand): Boolean {
        val trainsInTrash = trains.filter {
            it.dateOfDeletion != null
        }
        //Search brand in trash folder
        val index = trainsInTrash.indexOfFirst { it.brandName == item.brandName }
        return index != -1
    }

    override suspend fun deleteTrainsInTrashWithThisItem(item: Brand) {
        trains.removeAll {
            it.dateOfDeletion != null && it.brandName == item.brandName
        }
    }
}