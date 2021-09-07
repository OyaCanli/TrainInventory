package com.canlioya.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.canlioya.local.dao.BrandDao
import com.canlioya.local.dao.CategoryDao
import com.canlioya.local.dao.TrainDao
import com.canlioya.local.entities.BrandEntity
import com.canlioya.local.entities.CategoryEntity
import com.canlioya.local.entities.TrainEntity

@Database(
    entities = [TrainEntity::class, BrandEntity::class, CategoryEntity::class],
    version = 6, exportSchema = false
)
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

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE trains ADD COLUMN price REAL")
                database.execSQL("ALTER TABLE trains ADD COLUMN color TEXT")
            }
        }

        fun getInstance(context: Context): TrainDatabase {
            return sInstance ?: synchronized(this) {
                sInstance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrainDatabase::class.java, DATABASE_NAME
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                    .also { sInstance = it }
            }
        }
    }
}
