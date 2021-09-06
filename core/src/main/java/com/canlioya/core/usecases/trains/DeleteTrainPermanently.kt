package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class DeleteTrainPermanently(private val dataSource: ITrainDataSource) {

    suspend operator fun invoke(trainId: Int) = dataSource.deleteTrainPermanently(trainId)
}
