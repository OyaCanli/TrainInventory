package com.canlioya.core.usecases.brandcategory

import com.canlioya.core.data.IBrandCategoryDataSource

class GetItemNames<T: Any>(private val dataSource : IBrandCategoryDataSource<T>) {

    suspend operator fun invoke() = dataSource.getItemNames()
}