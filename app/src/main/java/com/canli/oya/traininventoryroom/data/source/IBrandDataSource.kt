package com.canli.oya.traininventoryroom.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

interface IBrandCategoryDataSource<T> {
    fun getAllItems() : LiveData<PagedList<T>>

    suspend fun insertItem(item: T)

    suspend fun updateItem(item: T)

    suspend fun deleteItem(item: T)

    fun isThisItemUsed(item: T): Boolean
}