package com.canli.oya.traininventoryroom.data.repositories

import androidx.lifecycle.LiveData
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.utils.AppExecutors

class CategoryRepository private constructor(private val mDatabase: TrainDatabase) {
    val categoryList: LiveData<List<String>>

    init {
        categoryList = loadCategories()
    }

    private fun loadCategories(): LiveData<List<String>> {
        return mDatabase.categoryDao().allCategories
    }

    fun insertCategory(category: CategoryEntry) {
        AppExecutors.instance.diskIO().execute { mDatabase.categoryDao().insertCategory(category) }
    }

    fun deleteCategory(category: CategoryEntry) {
        AppExecutors.instance.diskIO().execute { mDatabase.categoryDao().deleteCategory(category) }
    }

    fun isThisCategoryUsed(category: String): Boolean {
        return mDatabase.trainDao().isThisCategoryUsed(category)
    }

    companion object {

        private var sInstance: CategoryRepository? = null

        fun getInstance(database: TrainDatabase): CategoryRepository {
            return sInstance ?: synchronized(CategoryRepository::class.java) {
                sInstance ?: CategoryRepository(database).also { sInstance = it }
            }
        }
    }

}
