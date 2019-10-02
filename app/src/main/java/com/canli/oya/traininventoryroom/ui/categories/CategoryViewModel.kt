package com.canli.oya.traininventoryroom.ui.categories

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryDataSource
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.utils.UIState
import com.canli.oya.traininventoryroom.utils.provideCategoryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    val context: Context = application.applicationContext

    private val dataSource : CategoryDataSource = provideCategoryDataSource(context)

    var categoryListUiState = UIState(context.resources.getString(R.string.no_categories_found))

    var categoryList = dataSource.getAllCategories()

    private val _chosenCategory = MutableLiveData<CategoryEntry?>()

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