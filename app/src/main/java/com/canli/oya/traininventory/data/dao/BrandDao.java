package com.canli.oya.traininventory.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.canli.oya.traininventory.data.entities.BrandEntry;

import java.util.List;

@Dao
public interface BrandDao {

    @Query("SELECT * FROM brands")
    List<BrandEntry> getAllBrands();

    @Query("SELECT brandName FROM brands")
    LiveData<List<String>> getAllBrandNames();

    @Insert
    void insertBrand(BrandEntry brand);

    @Delete
    void deleteBrand(BrandEntry brand);
}
