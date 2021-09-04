package com.canli.oya.traininventoryroom.di


import com.canlioya.local.TrainDatabase
import com.canlioya.local.source.BrandDataSource
import com.canlioya.local.source.CategoryDataSource
import com.canlioya.local.source.TrainDataSource
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {


    @Singleton
    @Provides
    fun provideTrainDataSource(database: com.canlioya.local.TrainDatabase) : ITrainDataSource =
        com.canlioya.local.source.TrainDataSource(database)

    @Singleton
    @Provides
    fun provideCategoryDataSource(database: com.canlioya.local.TrainDatabase) : IBrandCategoryDataSource<Category> =
        com.canlioya.local.source.CategoryDataSource(database)

    @Singleton
    @Provides
    fun provideBrandDataSource(database: com.canlioya.local.TrainDatabase) : IBrandCategoryDataSource<Brand> =
        com.canlioya.local.source.BrandDataSource(database)

    @IODispatcher
    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}