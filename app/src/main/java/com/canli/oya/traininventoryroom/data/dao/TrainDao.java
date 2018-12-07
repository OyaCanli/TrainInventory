package com.canli.oya.traininventoryroom.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.canli.oya.traininventoryroom.data.entities.TrainEntry;

import java.util.List;

@Dao
public interface TrainDao {

    @Query("SELECT * FROM trains")
    LiveData<List<TrainEntry>> getAllTrains();

    @Query("SELECT * FROM trains WHERE trainId = :id")
    LiveData<TrainEntry> getChosenTrain(int id);

    @Query("SELECT 1 FROM trains WHERE brandName = :brandName")
    boolean isThisBrandUsed(String brandName);

    @Query("SELECT 1 FROM trains WHERE categoryName = :categoryName")
    boolean isThisCategoryUsed(String categoryName);

    @Query("SELECT * FROM trains WHERE brandName = :brandName")
    LiveData<List<TrainEntry>> getTrainsFromThisBrand(String brandName);

    @Query("SELECT * FROM trains WHERE categoryName = :categoryName")
    LiveData<List<TrainEntry>> getTrainsFromThisCategory(String categoryName);

    @Query("SELECT * FROM trains WHERE (trainName LIKE '%' || :query || '%') " +
            "OR (modelReference LIKE '%' || :query || '%') OR (description LIKE '%' || :query || '%')")
    List<TrainEntry> searchInTrains(String query);

    @Insert
    void insertTrain(TrainEntry train);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrainInfo(TrainEntry train);

    @Delete
    void deleteTrain(TrainEntry train);
}
