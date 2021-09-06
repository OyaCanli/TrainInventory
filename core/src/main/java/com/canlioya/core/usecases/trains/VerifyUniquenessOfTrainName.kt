package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class VerifyUniquenessOfTrainName(private val dataSource: ITrainDataSource) {

    suspend operator fun invoke(trainName: String) = dataSource.isThisTrainNameUsed(trainName)
}
