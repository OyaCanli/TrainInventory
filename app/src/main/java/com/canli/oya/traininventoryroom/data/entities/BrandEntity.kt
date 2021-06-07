package com.canli.oya.traininventoryroom.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "brands", indices = [Index(value = ["brandName"], unique = true)])
data class BrandEntity(
    @PrimaryKey(autoGenerate = true) var brandId: Int = 0,
    var brandName: String,
    var brandLogoUri: String? = null,
    var webUrl: String? = null)