package com.canli.oya.traininventoryroom.data.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "categories")
public class CategoryEntry {

    @NonNull
    @PrimaryKey
    private String categoryName;

    public CategoryEntry(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }
}
