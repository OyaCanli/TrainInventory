package com.canli.oya.traininventoryroom.di


import android.content.Context
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.source.*
import com.canlioya.core.data.IBrandCategoryDataSource
import com.canlioya.core.data.ITrainDataSource
import com.canlioya.core.models.Brand
import com.canlioya.core.models.Category
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DataSourceModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = TrainDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideTrainDataSource(database: TrainDatabase) : ITrainDataSource = TrainDataSource(database)

    @Singleton
    @Provides
    fun provideCategoryDataSource(database: TrainDatabase) : IBrandCategoryDataSource<Category> = CategoryDataSource(database)

    @Singleton
    @Provides
    fun provideBrandDataSource(database: TrainDatabase) : IBrandCategoryDataSource<Brand> = BrandDataSource(database)

    @IODispatcher
    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}