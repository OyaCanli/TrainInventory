package com.canlioya.core.data

import kotlinx.coroutines.flow.Flow

interface IBrandCategoryDataSource<T : Any> {

    fun getAllItems() : Flow<List<T>>

    suspend fun getItemNames() : List<String>

    suspend fun insertItem(item: T)

    suspend fun updateItem(item: T)

    suspend fun deleteItem(item: T)

    suspend fun isThisItemUsed(item: T): Boolean

    suspend fun isThisItemUsedInTrash(item: T): Boolean

    suspend fun deleteTrainsInTrashWithThisItem(item: T)
}