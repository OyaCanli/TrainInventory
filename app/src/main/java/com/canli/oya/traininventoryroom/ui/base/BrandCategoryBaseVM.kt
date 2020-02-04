package com.canli.oya.traininventoryroom.ui.base

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BrandCategoryBaseVM<T>(private val dataSource: IBrandCategoryDataSource<T>,
                                      private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : BaseListViewModel<T>() {

    override var allItems: LiveData<PagedList<T>> = dataSource.getAllItems()

    var isChildFragVisible: Boolean = false

    private val _chosenItem = MutableLiveData<T>()

    val chosenItem: LiveData<T>
        get() = _chosenItem

    fun setChosenItem(chosenItem: T) {
        _chosenItem.value = chosenItem
    }

    fun insertItem(item: T) {
        viewModelScope.launch(ioDispatcher) {
            try {
                dataSource.insertItem(item)
            } catch (e : SQLiteConstraintException){
                Timber.e("Same category exists")
            }
        }
    }

    suspend fun deleteItem(item: T) {
        dataSource.deleteItem(item)
    }

    fun updateItem(item: T) {
        viewModelScope.launch(ioDispatcher) { dataSource.updateItem(item) }
    }

    fun isThisItemUsed(item: T): Boolean {
        return dataSource.isThisItemUsed(item)
    }

}