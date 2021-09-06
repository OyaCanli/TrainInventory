package com.canli.oya.traininventoryroom.ui.base

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BCBaseViewModel<T : Any>(
    private val interactors: BrandCategoryInteractors<T>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var allItems: Flow<List<T>> = interactors.getAllItems()

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
                interactors.addItem(item)
            } catch (e: SQLiteConstraintException) {
                Timber.e("Same category exists")
            }
        }
    }

    suspend fun deleteItem(item: T) {
        viewModelScope.launch(ioDispatcher) {
            interactors.deleteItem(item)
        }
    }

    fun updateItem(item: T) {
        viewModelScope.launch(ioDispatcher) { interactors.updateItem(item) }
    }

    suspend fun isThisItemUsed(item: T): Boolean {
        return interactors.isThisItemUsed(item)
    }

    suspend fun isThisItemUsedInTrash(item: T): Boolean {
        return interactors.isThisItemUsedInTrash(item)
    }

    suspend fun deleteTrainsInTrashWithThisItem(item: T) {
        interactors.deleteTrainsInTrashWithThisItem(item)
    }
}
