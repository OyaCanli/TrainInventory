package com.canlioya.local.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.canlioya.core.models.Train

@Entity(
    tableName = "trains",
    foreignKeys = [
        ForeignKey(
            entity = BrandEntity::class,
            parentColumns = ["brandName"], childColumns = ["brandName"],
            onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryName"], childColumns = ["categoryName"], onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["brandName"]), Index(value = ["categoryName"])]
)
@Keep
data class TrainEntity(
    @PrimaryKey(autoGenerate = true) var trainId: Int = 0,
    var trainName: String? = null,
    var modelReference: String? = null,
    var brandName: String? = null,
    var categoryName: String? = null,
    var quantity: Int = 0,
    var imageUri: String? = null,
    var description: String? = null,
    var location: String? = null,
    var scale: String? = null,
    var dateOfDeletion: Long? = null
)

fun TrainEntity.toTrain() = Train(
    this.trainId,
    this.trainName,
    this.modelReference,
    this.brandName,
    this.categoryName,
    this.quantity,
    this.imageUri,
    this.description,
    this.location,
    this.scale,
    this.dateOfDeletion
)

fun Train.toTrainEntity() = TrainEntity(
    this.trainId,
    this.trainName,
    this.modelReference,
    this.brandName,
    this.categoryName,
    this.quantity,
    this.imageUri,
    this.description,
    this.location,
    this.scale,
    this.dateOfDeletion
)
