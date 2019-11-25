package com.canli.oya.traininventoryroom.data

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.paging.LimitOffsetDataSource
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

//Solution for unit testin pagedlist is taken from the project https://github.com/saied89/DVDPrism/

fun <T> List<T>.asPagedList(config: PagedList.Config? = null): PagedList<T>? {
    val pageSize = if(size == 0) 1 else size

    val defaultConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(pageSize)
            .setMaxSize(pageSize + 2)
            .setPrefetchDistance(1)
            .build()
    return LivePagedListBuilder<Int, T>(
            createMockDataSourceFactory(this),
            config ?: defaultConfig
    ).build().blockingObserve()
}

private fun <T> createMockDataSourceFactory(itemList: List<T>): DataSource.Factory<Int, T> =
        object : DataSource.Factory<Int, T>() {
            override fun create(): DataSource<Int, T> = MockLimitDataSource(itemList)
        }

private val mockQuery = mockk<RoomSQLiteQuery> {
    every { sql } returns ""
}

private val mockDb = mockk<RoomDatabase> {
    every { invalidationTracker } returns mockk(relaxUnitFun = true)
}

class MockLimitDataSource<T>(private val itemList: List<T>) : LimitOffsetDataSource<T>(mockDb, mockQuery, false, null) {
    override fun convertRows(cursor: Cursor?): MutableList<T> = itemList.toMutableList()
    override fun countItems(): Int = itemList.count()
    override fun isInvalid(): Boolean = false
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
//        callback.onResult(itemList.toMutableList())
    }

    override fun loadRange(startPosition: Int, loadCount: Int): MutableList<T> {
        return itemList.subList(startPosition, startPosition + loadCount).toMutableList()
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        callback.onResult(itemList, 0)
    }
}

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }

    observeForever(observer)

    latch.await(2, TimeUnit.SECONDS)
    return value
}
