package com.canli.oya.traininventoryroom.datasource

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainEntry

val sampleCategory1 = CategoryEntry(categoryName = "Wagon")
val sampleCategory2 = CategoryEntry(categoryName = "Locomotive")
val sampleCategory3 = CategoryEntry(categoryName = "Station")
val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2, sampleCategory3)

val sampleBrand1 = BrandEntry(brandName = "Marklin")
val sampleBrand2 = BrandEntry(brandName = "MDN")
val sampleBrand3 = BrandEntry(brandName = "Legit")
val sampleBrandList = mutableListOf(sampleBrand1, sampleBrand2, sampleBrand3)

val sampleTrain1 = TrainEntry(trainName = "Red Wagon", categoryName = sampleCategory1.categoryName, brandName = sampleBrand1.brandName, modelReference = "MN", description = "In very good state", quantity = 1, scale = "1.2", location = "2-A")
val sampleTrain2 = TrainEntry(trainName = "Blue Loco", categoryName = sampleCategory2.categoryName, brandName = sampleBrand2.brandName)
val sampleTrain3 = TrainEntry(trainName = "Orange TGV", categoryName = sampleCategory1.categoryName, brandName = sampleBrand1.brandName)
val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2)