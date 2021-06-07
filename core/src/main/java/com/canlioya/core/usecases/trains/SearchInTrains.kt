package com.canlioya.core.usecases.trains

import com.canlioya.core.data.ITrainDataSource

class SearchInTrains(private val dataSource : ITrainDataSource) {

    suspend operator fun invoke(keyword : String?, categoryName: String?, brandName : String?) = dataSource.searchInTrains(keyword, categoryName, brandName)
}