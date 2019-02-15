package com.canli.oya.traininventoryroom.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.canli.oya.traininventoryroom.data.dao.BrandDao
import com.canli.oya.traininventoryroom.data.dao.CategoryDao
import com.canli.oya.traininventoryroom.data.dao.TrainDao

@Database(entities = [TrainEntry::class, BrandEntry::class, CategoryEntry::class],
        version = 1, exportSchema = false)
abstract class TrainDatabase : RoomDatabase() {

    abstract fun trainDao(): TrainDao

    abstract fun brandDao(): BrandDao

    abstract fun categoryDao(): CategoryDao

    companion object {

        private const val DATABASE_NAME = "inventory"
        @Volatile private var sInstance: TrainDatabase? = null

        fun getInstance(context: Context): TrainDatabase {
            return sInstance ?: synchronized(this) {
                    Room.databaseBuilder(context.applicationContext,
                            TrainDatabase::class.java, TrainDatabase.DATABASE_NAME)
                            .build()
                            .also { sInstance = it }
                }

        }
    }
}
