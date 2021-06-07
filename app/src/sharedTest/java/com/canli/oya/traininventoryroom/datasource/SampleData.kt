package com.canli.oya.traininventoryroom.datasource

import com.canli.oya.traininventoryroom.data.BrandEntity
import com.canli.oya.traininventoryroom.data.CategoryEntity
import com.canli.oya.traininventoryroom.data.TrainEntity

val sampleCategory1 = CategoryEntity(categoryName = "Wagon")
val sampleCategory2 = CategoryEntity(categoryName = "Locomotive")
val sampleCategory3 = CategoryEntity(categoryName = "Station")
val sampleCategoryList = mutableListOf(sampleCategory1, sampleCategory2, sampleCategory3)

val sampleBrand1 = BrandEntity(brandName = "Marklin")
val sampleBrand2 = BrandEntity(brandName = "MDN")
val sampleBrand3 = BrandEntity(brandName = "Legit")
val sampleBrandList = mutableListOf(sampleBrand1, sampleBrand2, sampleBrand3)

val sampleTrain1 = TrainEntity(trainName = "Red Wagon", categoryName = sampleCategory1.categoryName, brandName = sampleBrand1.brandName, modelReference = "MN", description = "In very good state", quantity = 1, scale = "1.2", location = "2-A")
val sampleTrain2 = TrainEntity(trainName = "Blue Loco", categoryName = sampleCategory2.categoryName, brandName = sampleBrand2.brandName)
val sampleTrain3 = TrainEntity(trainName = "Orange TGV", categoryName = sampleCategory1.categoryName, brandName = sampleBrand1.brandName)
val sampleTrainList = mutableListOf(sampleTrain1, sampleTrain2)