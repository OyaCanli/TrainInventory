package com.canli.oya.traininventoryroom.data.source

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry

val sampleCategory1 = CategoryEntry(0, "Wagon")
val sampleCategory2 = CategoryEntry(1, "Locomotive")
val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2)

val sampleBrand1 = BrandEntry(0, "Marklin")
val sampleBrand2 = BrandEntry(1, "MDN")
val sampleBrand3 = BrandEntry(2, "Legit")
val sampleBrandList = mutableListOf(sampleBrand1, sampleBrand2)

val sampleTrain1 = TrainEntry(trainId = 0, trainName = "Red Wagon", categoryName = "Wagon", brandName = "Marklin", modelReference = "MN", description = "In very good state", quantity = 1, scale = "1.2", location = "2-A")
val sampleTrain2 = TrainEntry(trainId = 1, trainName = "Blue Loco", categoryName = "Locomotif", brandName = "MDN")
val sampleTrain3 = TrainEntry(trainId = 2, trainName = "Gare", categoryName = "Wagon", brandName = "Marklin")
val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2)