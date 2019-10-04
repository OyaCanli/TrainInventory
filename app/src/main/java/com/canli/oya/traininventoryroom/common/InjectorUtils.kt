package com.canli.oya.traininventoryroom.common

import android.content.Context
import com.canli.oya.traininventoryroom.data.*
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFactory


fun provideAddTrainFactory(context: Context, chosenTrain: TrainEntry?) : AddTrainFactory {
    return AddTrainFactory(provideTrainDataSource(context), provideCategoryDataSource(context), provideBrandDataSource(context), chosenTrain)
}

fun provideTrainDataSource(context: Context) : TrainDataSource {
    return TrainDataSource(provideDatabase(context))
}

fun provideCategoryDataSource(context: Context) : CategoryDataSource {
    return CategoryDataSource(provideDatabase(context))
}

fun provideBrandDataSource(context: Context) : BrandDataSource {
    return BrandDataSource(provideDatabase(context))
}

fun provideDatabase(context: Context) : TrainDatabase {
    return TrainDatabase.getInstance(context)
}

