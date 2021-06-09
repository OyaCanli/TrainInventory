package com.canli.oya.traininventoryroom.ui.brands


import androidx.hilt.lifecycle.ViewModelInject
import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canlioya.core.models.Brand
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class BrandViewModel @ViewModelInject constructor(interactors : BrandCategoryInteractors<Brand>,
                                                  @IODispatcher private val ioDispatcher: CoroutineDispatcher)
    : BCBaseViewModel<Brand>(interactors, ioDispatcher)