package com.canli.oya.traininventoryroom.data.source

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface IBrandCategoryDataSource<T : Any> {

    fun getAllPagedItems() : Flow<PagingData<T>>

    fun getAllItems() : Flow<List<T>>

    suspend fun insertItem(item: T)

    suspend fun updateItem(item: T)

    suspend fun deleteItem(item: T)

    fun isThisItemUsed(item: T): Boolean
}