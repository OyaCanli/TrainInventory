package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.*
import dagger.Binds
import dagger.Module

@Module
interface DataSourceModule {

    @Binds
    fun provideTrainDataSource(impl : TrainDataSource) : ITrainDataSource

    @Binds
    fun provideCategoryDataSource(impl : CategoryDataSource) : IBrandCategoryDataSource<CategoryEntry>

    @Binds
    fun provideBrandDataSource(impl : BrandDataSource) : IBrandCategoryDataSource<BrandEntry>
}