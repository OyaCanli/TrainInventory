package com.canli.oya.traininventoryroom.ui.categories


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.ICategoryDataSource
import com.canli.oya.traininventoryroom.ui.base.BaseListViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(private val dataSource : ICategoryDataSource,
                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : BaseListViewModel<CategoryEntry>() {

    override var allItems = dataSource.getAllCategories()

    private val _chosenCategory = MutableLiveData<CategoryEntry?>()

    var isChildFragVisible : Boolean = false

    val chosenCategory: LiveData<CategoryEntry?>
        get() = _chosenCategory

    fun setChosenCategory(chosenCategory: CategoryEntry) {
        _chosenCategory.value = chosenCategory
    }

    fun deleteCategory(category: CategoryEntry) {
        viewModelScope.launch(ioDispatcher) { dataSource.deleteCategory(category) }
    }

    fun insertCategory(category: CategoryEntry) {
        viewModelScope.launch(ioDispatcher) { dataSource.insertCategory(category) }
    }

    fun updateCategory(category: CategoryEntry){
        viewModelScope.launch(ioDispatcher) { dataSource.updateCategory(category) }
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return dataSource.isThisCategoryUsed(category)
    }
}