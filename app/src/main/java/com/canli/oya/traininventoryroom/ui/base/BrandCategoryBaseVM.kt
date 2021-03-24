package com.canli.oya.traininventoryroom.ui.base

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.UIState
import com.canli.oya.traininventoryroom.data.source.BrandDataSource
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BrandCategoryBaseVM<T : Any>(private val dataSource: IBrandCategoryDataSource<T>,
                                      private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var allPagedItems: Flow<PagingData<T>> = dataSource.getAllPagedItems()

    var allItems: Flow<List<T>> = dataSource.getAllItems()

    private val emptyMessage = if(dataSource is BrandDataSource) R.string.no_brands_found else R.string.no_categories_found
    var listUiState : UIState = UIState(emptyMessage)

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

    fun isThisItemUsed(item: T): Boolean {
        return dataSource.isThisItemUsed(item)
    }

}