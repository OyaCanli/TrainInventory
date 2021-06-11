package com.canli.oya.traininventoryroom.datasource

import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.usecases.brandcategory.*
import com.canlioya.core.usecases.trains.*

fun provideTrainInteractor(dataSource : ITrainDataSource) = TrainInteractors(
    AddTrain(dataSource), UpdateTrain(dataSource), SendTrainToTrash(dataSource),
    GetAllTrains(dataSource), GetChosenTrain(dataSource), DeleteTrainPermanently(dataSource),
    GetAllTrainsInTrash(dataSource), RestoreTrainFromTrash(dataSource),
    VerifyUniquenessOfTrainName(dataSource), SearchInTrains(dataSource)
)

fun provideBrandInteractor(dataSource : IBrandCategoryDataSource<Brand>) : BrandCategoryInteractors<Brand> = BrandCategoryInteractors(
    AddItem(dataSource), UpdateItem(dataSource), DeleteItem(dataSource), GetAllItems(dataSource),
    IsThisItemUsed(dataSource), IsThisItemUsedInTrash(dataSource), DeleteTrainsInTrashWithThisItem(dataSource),
    GetItemNames(dataSource)
)

fun provideCategoryInteractor(dataSource : IBrandCategoryDataSource<Category>) : BrandCategoryInteractors<Category> = BrandCategoryInteractors(
    AddItem(dataSource), UpdateItem(dataSource), DeleteItem(dataSource), GetAllItems(dataSource),
    IsThisItemUsed(dataSource), IsThisItemUsedInTrash(dataSource), DeleteTrainsInTrashWithThisItem(dataSource),
    GetItemNames(dataSource)
)