package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class GetChosenTrain(private val dataSource: ITrainDataSource) {

    operator fun invoke(trainId: Int) = dataSource.getChosenTrain(trainId)
}
