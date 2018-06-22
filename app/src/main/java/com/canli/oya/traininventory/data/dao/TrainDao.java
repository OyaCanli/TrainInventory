package com.canli.oya.traininventory.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.data.TrainMinimal;

import java.util.List;

@Dao
public interface TrainDao {

    @Query("SELECT trainName, modelReference, brandName, categoryName, imageUri, trainId FROM trains")
    LiveData<List<TrainMinimal>> getAllTrains();

    @Query("SELECT * FROM trains WHERE trainId = :id")
    LiveData<TrainEntry> getChosenTrain(int id);

    @Insert
    void insertTrain(TrainEntry train);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrainInfo(TrainEntry train);

    @Delete
    void deleteTrain(TrainEntry train);
}
