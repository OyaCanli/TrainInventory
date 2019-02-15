package com.canli.oya.traininventoryroom.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.ForeignKey.RESTRICT
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "trains",
        foreignKeys = [ForeignKey(entity = BrandEntry::class,
                parentColumns = ["brandName"], childColumns = ["brandName"],
                onUpdate = CASCADE, onDelete = RESTRICT),
            ForeignKey(entity = CategoryEntry::class,
                    parentColumns = ["categoryName"], childColumns = ["categoryName"])],
        indices = [Index(value = ["brandName"]), Index(value = ["categoryName"])])
data class TrainEntry(
        @PrimaryKey(autoGenerate = true) var trainId: Int = 0,
        var trainName: String? = null,
        var modelReference: String? = null,
        var brandName: String? = null,
        var categoryName: String? = null,
        var quantity: Int = 0,
        var imageUri: String? = null,
        var description: String? = null,
        var location: String? = null,
        var scale: String? = null)


@Entity(tableName = "brands", indices = [Index(value = ["brandName"], unique = true)])
data class BrandEntry(
        @PrimaryKey(autoGenerate = true) var brandId: Int = 0,
        var brandName: String? = null,
        var brandLogoUri: String? = null,
        var webUrl: String? = null)


@Entity(tableName = "categories")
data class CategoryEntry(@field:PrimaryKey
                         var categoryName: String)
