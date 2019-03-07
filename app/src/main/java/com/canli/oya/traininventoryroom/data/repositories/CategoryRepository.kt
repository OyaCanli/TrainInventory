package com.canli.oya.traininventoryroom.data.repositories

import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase

class CategoryRepository private constructor(private val mDatabase: TrainDatabase) {

    fun getCategoryList() = mDatabase.categoryDao().allCategories

    suspend fun insertCategory(category: CategoryEntry) {
       mDatabase.categoryDao().insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntry) {
        mDatabase.categoryDao().deleteCategory(category)
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
