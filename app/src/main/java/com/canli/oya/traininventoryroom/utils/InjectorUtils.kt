package com.canli.oya.traininventoryroom.utils

import android.content.Context

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.repositories.BrandRepository
import com.canli.oya.traininventoryroom.data.repositories.CategoryRepository
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository

fun provideTrainRepo(context: Context): TrainRepository {
    val db = TrainDatabase.getInstance(context)
    return TrainRepository.getInstance(db)
}

fun provideBrandRepo(context: Context): BrandRepository {
    val db = TrainDatabase.getInstance(context)
    return BrandRepository.getInstance(db)
}

fun provideCategoryRepo(context: Context): CategoryRepository {
    val db = TrainDatabase.getInstance(context)
    return CategoryRepository.getInstance(db)
}

