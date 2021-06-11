package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Train

class UpdateTrain(private val dataSource : ITrainDataSource) {

    suspend operator fun invoke(train : Train) = dataSource.updateTrain(train)
}