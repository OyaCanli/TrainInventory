package com.canli.oya.traininventoryroom.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.canli.oya.traininventoryroom.data.entities.CategoryEntry;

import java.util.List;

@Dao
public interface CategoryDao {

   @Query("SELECT * FROM categories")
    LiveData<List<String>> getAllCategories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(CategoryEntry category);

    @Delete
    void deleteCategory(CategoryEntry category);

}
