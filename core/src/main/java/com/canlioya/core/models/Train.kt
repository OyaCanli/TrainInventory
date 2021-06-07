package com.canlioya.core.models


data class Train(
    var trainId: Int = 0,
    var trainName: String? = null,
    var modelReference: String? = null,
    var brandName: String? = null,
    var categoryName: String? = null,
    var quantity: Int = 0,
    var imageUri: String? = null,
    var description: String? = null,
    var location: String? = null,
    var scale: String? = null,
    var dateOfDeletion: Long? = null)
