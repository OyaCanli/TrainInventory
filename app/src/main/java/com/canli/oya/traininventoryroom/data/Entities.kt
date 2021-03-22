package com.canli.oya.traininventoryroom.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.RESTRICT
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "trains",
        foreignKeys = [ForeignKey(entity = BrandEntry::class,
                parentColumns = ["brandName"], childColumns = ["brandName"],
                onUpdate = CASCADE, onDelete = RESTRICT),
            ForeignKey(entity = CategoryEntry::class,
                    parentColumns = ["categoryName"], childColumns = ["categoryName"], onUpdate = CASCADE, onDelete = RESTRICT)],
        indices = [Index(value = ["brandName"]), Index(value = ["categoryName"])])
@Keep
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
        var scale: String? = null) : Parcelable

data class TrainMinimal(val trainId: Int = 0,
                        val trainName: String? = null,
                        val modelReference: String? = null,
                        val brandName: String? = null,
                        val categoryName: String? = null,
                        val imageUri: String? = null)


@Entity(tableName = "brands", indices = [Index(value = ["brandName"], unique = true)])
data class BrandEntry(
        @PrimaryKey(autoGenerate = true) var brandId: Int = 0,
        var brandName: String,
        var brandLogoUri: String? = null,
        var webUrl: String? = null)


@Entity(tableName = "categories", indices = [Index(value = ["categoryName"], unique = true)])
data class CategoryEntry(
        @field:PrimaryKey(autoGenerate = true) var categoryId : Int = 0,
        var categoryName: String)
