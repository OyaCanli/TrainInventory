package com.canli.oya.traininventoryroom.ui.brands


import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource


import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class BrandViewModel(dataSource : IBrandCategoryDataSource<BrandEntity>,
                     ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : BCBaseViewModel<BrandEntity>(dataSource, ioDispatcher)