package com.canli.oya.traininventoryroom.ui.categories

import com.canli.oya.traininventoryroom.di.IODispatcher
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canlioya.core.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    interactors: BrandCategoryInteractors<Category>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) :
    BCBaseViewModel<Category>(interactors, ioDispatcher)
