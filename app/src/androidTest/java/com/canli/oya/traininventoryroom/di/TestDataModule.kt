package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
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
    fun provideCategoryDataSource() : IBrandCategoryDataSource<CategoryEntry> = FakeCategoryDataSource()

    @Provides
    @Singleton
    fun provideBrandDataSource() : IBrandCategoryDataSource<BrandEntry> = FakeBrandDataSource()
}