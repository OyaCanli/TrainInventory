package com.canli.oya.traininventoryroom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.canli.oya.traininventoryroom.data.dao.BrandDao
import com.canli.oya.traininventoryroom.data.dao.CategoryDao
import com.canli.oya.traininventoryroom.data.dao.TrainDao

@Database(entities = [TrainEntry::class, BrandEntry::class, CategoryEntry::class],
        version = 5, exportSchema = false)
abstract class TrainDatabase : RoomDatabase() {

    abstract fun trainDao(): TrainDao

    abstract fun brandDao(): BrandDao

    abstract fun categoryDao(): CategoryDao

    companion object {

        private const val DATABASE_NAME = "train_inventory"
        @Volatile private var sInstance: TrainDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE trains ADD COLUMN dateOfDeletion INTEGER")
            }
        }

        fun getInstance(context: Context): TrainDatabase {
            return sInstance ?: synchronized(this) {
                    sInstance ?: Room.databaseBuilder(context.applicationContext,
                            TrainDatabase::class.java, DATABASE_NAME)
                            .addMigrations(MIGRATION_4_5)
                            .build()
                            .also { sInstance = it }
                }
        }
    }
}
