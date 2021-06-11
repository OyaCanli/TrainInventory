package com.canli.oya.traininventoryroom.datasource

import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.models.Train


val sampleCategory1 = Category(categoryName = "Wagon")
val sampleCategory2 = Category(categoryName = "Locomotive")
val sampleCategory3 = Category(categoryName = "Station")
val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2, sampleCategory3)

val sampleBrand1 = Brand(brandName = "Marklin")
val sampleBrand2 = Brand(brandName = "MDN")
val sampleBrand3 = Brand(brandName = "Legit")
val sampleBrandList = mutableListOf(sampleBrand1, sampleBrand2, sampleBrand3)

val sampleTrain1 = Train(trainId = 101, trainName = "Red Wagon", categoryName = sampleCategory1.categoryName, brandName = sampleBrand1.brandName, modelReference = "MN", description = "In very good state", quantity = 1, scale = "1.2", location = "2-A")
val sampleTrain2 = Train(trainId = 202, trainName = "Blue Loco", categoryName = sampleCategory2.categoryName, brandName = sampleBrand2.brandName)
val sampleTrain3 = Train(trainId = 303, trainName = "Orange TGV", categoryName = sampleCategory1.categoryName, brandName = sampleBrand1.brandName)
val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2)