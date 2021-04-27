package com.canli.oya.traininventoryroom.ui.base

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.source.BrandDataSource
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BCBaseViewModel<T : Any>(private val dataSource: IBrandCategoryDataSource<T>,
                                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var allItems: Flow<List<T>> = dataSource.getAllItems()

    val emptyMessage = if(dataSource is BrandDataSource) R.string.no_brands_found else R.string.no_categories_found

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
        viewModelScope.launch(ioDispatcher){
            dataSource.deleteItem(item)
        }
    }

    fun updateItem(item: T) {
        viewModelScope.launch(ioDispatcher) { dataSource.updateItem(item) }
    }

    suspend fun isThisItemUsed(item: T): Boolean {
        return dataSource.isThisItemUsed(item) != null
    }

    suspend fun isThisItemUsedInTrash(item: T): Boolean {
        return dataSource.isThisItemUsedInTrash(item) != null
    }

    suspend fun deleteTrainsInTrashWithThisItem(item: T){
        dataSource.deleteTrainsInTrashWithThisItem(item)
    }

}