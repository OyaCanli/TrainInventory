package com.canli.oya.traininventoryroom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.canli.oya.traininventoryroom.data.dao.BrandDao
import com.canli.oya.traininventoryroom.data.dao.CategoryDao
import com.canli.oya.traininventoryroom.data.dao.TrainDao

@Database(entities = [TrainEntry::class, BrandEntry::class, CategoryEntry::class],
        version = 4, exportSchema = false)
abstract class TrainDatabase : RoomDatabase() {

    abstract fun trainDao(): TrainDao

    abstract fun brandDao(): BrandDao

    abstract fun categoryDao(): CategoryDao

    companion object {

        private const val DATABASE_NAME = "train_inventory"
        @Volatile private var sInstance: TrainDatabase? = null

        fun getInstance(context: Context): TrainDatabase {
            return sInstance ?: synchronized(this) {
                    sInstance ?: Room.databaseBuilder(context.applicationContext,
                            TrainDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                            .also { sInstance = it }
                }

        }
    }
}
