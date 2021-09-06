package com.canli.oya.traininventoryroom.ui.brands

import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canlioya.core.models.Brand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class BrandViewModel @Inject constructor(
    interactors: BrandCategoryInteractors<Brand>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) :
    BCBaseViewModel<Brand>(interactors, ioDispatcher)
