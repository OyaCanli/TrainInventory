package com.canli.oya.traininventoryroom.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.canli.oya.traininventoryroom.data.entities.BrandEntry;

import java.util.List;

@Dao
public interface BrandDao {

    @Query("SELECT * FROM brands")
    LiveData<List<BrandEntry>> getAllBrands();

    @Query("SELECT * FROM brands WHERE brandId = :id")
    LiveData<BrandEntry> getChosenBrand(int id);

    @Insert
    void insertBrand(BrandEntry brand);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBrandInfo(BrandEntry brand);

    @Delete
    void deleteBrand(BrandEntry brand);
}
