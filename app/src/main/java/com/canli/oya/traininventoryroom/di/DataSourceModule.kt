package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.entities.BrandEntity
import com.canli.oya.traininventoryroom.data.entities.CategoryEntity
import com.canli.oya.traininventoryroom.data.source.*
import dagger.Binds
import dagger.Module

@Module
interface DataSourceModule {

    @Binds
    fun provideTrainDataSource(impl : TrainDataSource) : ITrainDataSource

    @Binds
    fun provideCategoryDataSource(impl : CategoryDataSource) : IBrandCategoryDataSource<CategoryEntity>

    @Binds
    fun provideBrandDataSource(impl : BrandDataSource) : IBrandCategoryDataSource<BrandEntity>
}