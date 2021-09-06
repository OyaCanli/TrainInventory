package com.canlioya.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.canlioya.core.models.Brand

@Entity(tableName = "brands", indices = [Index(value = ["brandName"], unique = true)])
data class BrandEntity(
    @PrimaryKey(autoGenerate = true) var brandId: Int = 0,
    var brandName: String,
    var brandLogoUri: String? = null,
    var webUrl: String? = null
)

fun BrandEntity.toBrand() =
    Brand(this.brandId, this.brandName, this.brandLogoUri, this.webUrl)

fun Brand.toBrandEntity() = BrandEntity(this.brandId, this.brandName, this.brandLogoUri, this.webUrl)

fun List<BrandEntity>.toBrandList() = this.map { it.toBrand() }
