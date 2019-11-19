package com.canli.oya.traininventoryroom.ui.categories


import android.content.res.Resources
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState
import com.canli.oya.traininventoryroom.data.CategoryDataSource
import com.canli.oya.traininventoryroom.data.CategoryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoryViewModel(private val dataSource : CategoryDataSource, resources : Resources) : ViewModel() {

    var categoryListUiState = UIState(resources.getString(R.string.no_categories_found))

    var categoryList : LiveData<PagedList<CategoryEntry>> = dataSource.getAllCategories()

    private val _chosenCategory = MutableLiveData<CategoryEntry?>()

    private var _isChildFragVisible = MutableLiveData<Boolean>()

    init {
        _isChildFragVisible.value = false
    }

    var isChildFragVisible : LiveData<Boolean> = _isChildFragVisible
        private set

    fun setIsChildFragVisible(isVisible : Boolean) {
        _isChildFragVisible.value = isVisible
        Timber.d("isChildFragVisible is set to $isVisible")
    }

    val chosenCategory: LiveData<CategoryEntry?>
        get() = _chosenCategory

    fun setChosenCategory(chosenCategory: CategoryEntry) {
        _chosenCategory.value = chosenCategory
    }

    fun deleteCategory(category: CategoryEntry) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.deleteCategory(category) }
    }

    fun insertCategory(category: CategoryEntry) {
        viewModelScope.launch(Dispatchers.IO) { dataSource.insertCategory(category) }
    }

    fun updateCategory(category: CategoryEntry){
        viewModelScope.launch(Dispatchers.IO) { dataSource.updateCategory(category) }
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return dataSource.isThisCategoryUsed(category)
    }
}