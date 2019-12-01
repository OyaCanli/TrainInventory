package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.source.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TestDataModule {

    @Provides
    @Singleton
    fun provideTrainDataSource() : ITrainDataSource = FakeTrainDataSource()

    @Provides
    @Singleton
    fun provideCategoryDataSource() : ICategoryDataSource = FakeCategoryDataSource()

    @Provides
    @Singleton
    fun provideBrandDataSource() : IBrandDataSource = FakeBrandDataSource()
}