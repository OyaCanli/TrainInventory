package com.canli.oya.traininventoryroom.utils

import android.content.Context

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.data.datasources.BrandDataSource
import com.canli.oya.traininventoryroom.data.datasources.CategoryDataSource
import com.canli.oya.traininventoryroom.data.datasources.TrainDataSource
import com.canli.oya.traininventoryroom.data.repositories.BrandRepository
import com.canli.oya.traininventoryroom.data.repositories.CategoryRepository
import com.canli.oya.traininventoryroom.data.repositories.TrainRepository
import com.canli.oya.traininventoryroom.viewmodel.AddTrainFactory

fun provideTrainRepo(context: Context): TrainRepository {
    val db = TrainDatabase.getInstance(context)
    return TrainRepository(TrainDataSource(db), CategoryDataSource(db), BrandDataSource(db))
}

fun provideBrandRepo(context: Context): BrandRepository {
    val db = TrainDatabase.getInstance(context)
    return BrandRepository(BrandDataSource(db))
}

fun provideCategoryRepo(context: Context): CategoryRepository {
    val db = TrainDatabase.getInstance(context)
    return CategoryRepository(CategoryDataSource(db))
}

fun provideAddTrainFactory(context: Context, chosenTrain: TrainEntry?) : AddTrainFactory{
    return AddTrainFactory(provideTrainRepo(context), chosenTrain)
}

