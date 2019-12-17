package com.canli.oya.traininventoryroom.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BrandCategoryBaseVM<T>(private val dataSource : IBrandCategoryDataSource<T>,
                                      private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : BaseListViewModel<T>() {

    override var allItems: LiveData<PagedList<T>> = dataSource.getAllItems()

    var isChildFragVisible : Boolean = false

    private val _chosenItem = MutableLiveData<T>()

    val chosenItem: LiveData<T>
        get() = _chosenItem

    fun setChosenItem(chosenItem: T) {
        _chosenItem.value = chosenItem
    }

    fun insertItem(item : T) {
        viewModelScope.launch(ioDispatcher) { dataSource.insertItem(item) }
    }

    suspend fun deleteItem(item : T) {
        dataSource.deleteItem(item)
    }

    fun updateItem(item : T) {
        viewModelScope.launch(ioDispatcher) { dataSource.insertItem(item) }
    }

    fun isThisItemUsed(itemName: String): Boolean {
        return dataSource.isThisItemUsed(itemName)
    }

}