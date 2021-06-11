package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class SendTrainToTrash(private val dataSource : ITrainDataSource) {

    suspend operator fun invoke(trainId : Int, dateOfDeletion : Long) = dataSource.sendTrainToTrash(trainId, dateOfDeletion)
}