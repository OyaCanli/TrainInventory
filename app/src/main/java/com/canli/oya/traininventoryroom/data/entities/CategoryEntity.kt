package com.canli.oya.traininventoryroom.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.canlioya.core.models.Category

@Entity(tableName = "categories", indices = [Index(value = ["categoryName"], unique = true)])
data class CategoryEntity(
    @field:PrimaryKey(autoGenerate = true) var categoryId : Int = 0,
    var categoryName: String)

fun CategoryEntity.toCategory() =
    Category(this.categoryId, this.categoryName)

fun Category.toCategoryEntity() = CategoryEntity(this.categoryId, this.categoryName)

fun List<CategoryEntity>.toCategoryList() = this.map { it.toCategory() }
