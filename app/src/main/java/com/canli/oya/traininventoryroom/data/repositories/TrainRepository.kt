package com.canli.oya.traininventoryroom.data.repositories

import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.datasources.BrandDataSource
import com.canli.oya.traininventoryroom.data.datasources.CategoryDataSource
import com.canli.oya.traininventoryroom.data.datasources.TrainDataSource
import io.reactivex.Flowable

class TrainRepository(private val trainDataSource: TrainDataSource,
                                          private val categoryDataSource: CategoryDataSource,
                                          private val brandDataSource: BrandDataSource) {

    fun getAllTrains(): Flowable<List<TrainEntry>> {
        return trainDataSource.getAllTrains()
    }

    fun getChosenTrainLiveData(trainId : Int): Flowable<TrainEntry> {
        return trainDataSource.getChosenTrainLiveData(trainId)
    }

    suspend fun insertTrain(train: TrainEntry) {
        trainDataSource.insertTrain(train)
    }

    suspend fun updateTrain(train: TrainEntry) {
        trainDataSource.updateTrain(train)
    }

    suspend fun deleteTrain(train: TrainEntry) {
        trainDataSource.deleteTrain(train)
    }

    fun getTrainsFromThisBrand(brandName: String): Flowable<List<TrainEntry>> {
        return trainDataSource.getTrainsFromThisBrand(brandName)
    }

    fun getTrainsFromThisCategory(category: String): Flowable<List<TrainEntry>> {
        return trainDataSource.getTrainsFromThisCategory(category)
    }

    suspend fun searchInTrains(query: String): List<TrainEntry> {
        return trainDataSource.searchInTrains(query)
    }

    fun getAllBrands() = brandDataSource.getAllBrands()

    fun getAllCategories() = categoryDataSource.getAllCategories()
}
