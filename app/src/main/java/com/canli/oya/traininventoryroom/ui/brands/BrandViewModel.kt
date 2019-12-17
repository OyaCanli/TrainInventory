package com.canli.oya.traininventoryroom.ui.brands


import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource


import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseVM
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class BrandViewModel(dataSource : IBrandCategoryDataSource<BrandEntry>,
                     ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : BrandCategoryBaseVM<BrandEntry>(dataSource, ioDispatcher)