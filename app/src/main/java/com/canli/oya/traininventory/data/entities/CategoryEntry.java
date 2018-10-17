package com.canli.oya.traininventory.data.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "categories")
public class CategoryEntry {

    @NonNull
    @PrimaryKey
    private String categoryName;

    public CategoryEntry(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
