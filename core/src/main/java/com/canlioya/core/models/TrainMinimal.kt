package com.canlioya.core.models

data class TrainMinimal(
    var trainId: Int = 0,
    var trainName: String? = null,
    var modelReference: String? = null,
    var brandName: String? = null,
    var categoryName: String? = null,
    var imageUri: String? = null
)
