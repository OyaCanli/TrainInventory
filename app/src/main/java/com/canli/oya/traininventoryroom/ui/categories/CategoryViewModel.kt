package com.canli.oya.traininventoryroom.ui.categories


import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.UIState
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.ICategoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoryViewModel(private val dataSource : ICategoryDataSource,
                        resources : Resources,
                        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    var categoryListUiState = UIState(resources.getString(R.string.no_categories_found))

    var categoryList = dataSource.getAllCategories()

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