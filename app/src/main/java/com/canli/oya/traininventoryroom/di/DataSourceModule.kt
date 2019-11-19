package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.datasource.*
import dagger.Binds
import dagger.Module

@Module
interface DataSourceModule {

    @Binds
    fun provideTrainDataSource(impl : TrainDataSource) : ITrainDataSource

    @Binds
    fun provideCategoryDataSource(impl : CategoryDataSource) : ICategoryDataSource

    @Binds
    fun provideBrandDataSource(impl : BrandDataSource) : IBrandDataSource
}