package com.canli.oya.traininventoryroom.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState

abstract class BaseListViewModel<T> : ViewModel(){

    var listUiState : UIState = UIState(message = R.string.no_items_found)

    abstract var allItems : LiveData<PagedList<T>>

}