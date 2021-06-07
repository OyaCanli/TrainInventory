package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.interactors.BrandCategoryInteractors
import com.canli.oya.traininventoryroom.interactors.TrainInteractors
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import com.canlioya.core.usecases.brandcategory.*
import com.canlioya.core.usecases.trains.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InteractorsModule {

    @Provides
    @Singleton
    fun provideTrainInteractor(dataSource : ITrainDataSource) = TrainInteractors(
        AddTrain(dataSource),
    UpdateTrain(dataSource), SendTrainToTrash(dataSource), GetAllTrains(dataSource), GetChosenTrain(dataSource),
        DeleteTrainPermanently(dataSource), GetAllTrainsInTrash(dataSource), RestoreTrainFromTrash(dataSource),
        VerifyUniquenessOfTrainName(dataSource), SearchInTrains(dataSource)
    )

    @Provides
    @Singleton
    fun provideBrandInteractor(dataSource : IBrandCategoryDataSource<Brand>) : BrandCategoryInteractors<Brand> = BrandCategoryInteractors(
        AddItem(dataSource), UpdateItem(dataSource), DeleteItem(dataSource), GetAllItems(dataSource),
        IsThisItemUsed(dataSource), IsThisItemUsedInTrash(dataSource), DeleteTrainsInTrashWithThisItem(dataSource),
        GetItemNames(dataSource)
    )

    @Provides
    @Singleton
    fun provideCategoryInteractor(dataSource : IBrandCategoryDataSource<Category>) : BrandCategoryInteractors<Category> = BrandCategoryInteractors(
        AddItem(dataSource), UpdateItem(dataSource), DeleteItem(dataSource), GetAllItems(dataSource),
        IsThisItemUsed(dataSource), IsThisItemUsedInTrash(dataSource), DeleteTrainsInTrashWithThisItem(dataSource),
        GetItemNames(dataSource)
    )

}