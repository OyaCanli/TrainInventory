package com.canlioya.core.usecases.brandcategory

import com.canlioya.core.data.IBrandCategoryDataSource

class IsThisItemUsedInTrash <T : Any>(private val dataSource: IBrandCategoryDataSource<T>) {

    suspend operator fun invoke(item: T) = dataSource.isThisItemUsedInTrash(item)
}
