package com.canli.oya.traininventory.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.canli.oya.traininventory.data.entities.CategoryEntry;

import java.util.List;

@Dao
public interface CategoryDao {

   /* @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntry>> getAllCategories();*/

    @Query("SELECT categoryName FROM categories")
    LiveData<List<String>> getAllCategoryNames();

    @Insert
    void insertCategory(CategoryEntry category);

    @Delete
    void deleteCategory(CategoryEntry category);

}
