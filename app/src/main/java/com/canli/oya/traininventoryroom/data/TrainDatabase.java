package com.canli.oya.traininventoryroom.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.canli.oya.traininventoryroom.data.dao.BrandDao;
import com.canli.oya.traininventoryroom.data.dao.CategoryDao;
import com.canli.oya.traininventoryroom.data.dao.TrainDao;
import com.canli.oya.traininventoryroom.data.entities.BrandEntry;
import com.canli.oya.traininventoryroom.data.entities.CategoryEntry;
import com.canli.oya.traininventoryroom.data.entities.TrainEntry;

@Database(entities = {TrainEntry.class, BrandEntry.class, CategoryEntry.class}, version =1, exportSchema = false)
public abstract class TrainDatabase extends RoomDatabase{

    private static final String LOG_TAG = TrainDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "inventory";
    private static TrainDatabase sInstance;

    public static TrainDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TrainDatabase.class, TrainDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract TrainDao trainDao();

    public abstract BrandDao brandDao();

    public abstract CategoryDao categoryDao();
}
