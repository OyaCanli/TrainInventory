package com.canli.oya.traininventoryroom.data.source

import kotlinx.coroutines.flow.Flow

interface IBrandCategoryDataSource<T : Any> {

    fun getAllItems() : Flow<List<T>>

    suspend fun getItemNames() : List<String>

    suspend fun insertItem(item: T)

    suspend fun updateItem(item: T)

    suspend fun deleteItem(item: T)

    suspend fun isThisItemUsed(item: T): Int?

    suspend fun isThisItemUsedInTrash(item: T): Int?

    suspend fun deleteTrainsInTrashWithThisItem(item: T)
}