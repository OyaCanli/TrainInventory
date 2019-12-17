package com.canli.oya.traininventoryroom.ui.categories


import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseVM
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CategoryViewModel(dataSource : IBrandCategoryDataSource<CategoryEntry>,
                        ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : BrandCategoryBaseVM<CategoryEntry>(dataSource, ioDispatcher)