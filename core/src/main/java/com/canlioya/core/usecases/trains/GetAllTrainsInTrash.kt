package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class GetAllTrainsInTrash(private val dataSource: ITrainDataSource) {

    suspend operator fun invoke() = dataSource.getAllTrainsInTrash()
}
