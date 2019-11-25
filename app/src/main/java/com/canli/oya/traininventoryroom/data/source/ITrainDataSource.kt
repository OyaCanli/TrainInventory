package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.TrainMinimal

interface ITrainDataSource {

    fun getAllTrains() : LiveData<PagedList<TrainMinimal>>

    fun getChosenTrain(trainId : Int): LiveData<TrainEntry>

    suspend fun insertTrain(train: TrainEntry)

    suspend fun updateTrain(train: TrainEntry)

    suspend fun deleteTrain(train: TrainEntry)

    suspend fun deleteTrain(trainId: Int)

    fun getTrainsFromThisBrand(brandName: String): LiveData<PagedList<TrainMinimal>>

    fun getTrainsFromThisCategory(category: String): LiveData<PagedList<TrainMinimal>>

    fun searchInTrains(query: String): LiveData<PagedList<TrainMinimal>>
}