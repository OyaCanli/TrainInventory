package com.canli.oya.traininventoryroom.data.source

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface IBrandCategoryDataSource<T : Any> {

    fun getAllPagedItems() : Flow<PagingData<T>>

    fun getAllItems() : Flow<List<T>>

    suspend fun getItemNames() : List<String>

    suspend fun insertItem(item: T)

    suspend fun updateItem(item: T)

    suspend fun deleteItem(item: T)

    suspend fun isThisItemUsed(item: T): Int?
}