package com.canlioya.core.usecases.brandcategory

import com.canlioya.core.data.IBrandCategoryDataSource

class GetAllItems<T : Any>(private val dataSource: IBrandCategoryDataSource<T>) {

    operator fun invoke() = dataSource.getAllItems()
}
