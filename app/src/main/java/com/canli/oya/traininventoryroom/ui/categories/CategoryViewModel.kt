package com.canli.oya.traininventoryroom.ui.categories



import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CategoryViewModel(dataSource : IBrandCategoryDataSource<CategoryEntity>,
                        ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : BCBaseViewModel<CategoryEntity>(dataSource, ioDispatcher)