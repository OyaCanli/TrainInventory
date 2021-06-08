package com.canli.oya.traininventoryroom.ui.categories


import androidx.hilt.lifecycle.ViewModelInject
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canlioya.core.models.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CategoryViewModel @ViewModelInject constructor(interactors : BrandCategoryInteractors<Category>,
                                                     ioDispatcher: CoroutineDispatcher = Dispatchers.IO)
    : BCBaseViewModel<Category>(interactors, ioDispatcher)