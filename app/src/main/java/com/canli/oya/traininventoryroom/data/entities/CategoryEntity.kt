package com.canli.oya.traininventoryroom.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories", indices = [Index(value = ["categoryName"], unique = true)])
data class CategoryEntity(
    @field:PrimaryKey(autoGenerate = true) var categoryId : Int = 0,
    var categoryName: String)