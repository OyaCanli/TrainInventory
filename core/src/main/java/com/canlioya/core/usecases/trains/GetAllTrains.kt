package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class GetAllTrains(private val dataSource: ITrainDataSource) {

    operator fun invoke() = dataSource.getAllTrains()
}
