package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Train

class AddTrain(private val dataSource: ITrainDataSource) {

    suspend operator fun invoke(train: Train) = dataSource.insertTrain(train)
}
