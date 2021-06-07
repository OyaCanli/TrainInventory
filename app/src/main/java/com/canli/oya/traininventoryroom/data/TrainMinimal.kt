package com.canli.oya.traininventoryroom.data


data class TrainMinimal(val trainId: Int = 0,
                        val trainName: String? = null,
                        val modelReference: String? = null,
                        val brandName: String? = null,
                        val categoryName: String? = null,
                        val imageUri: String? = null)

