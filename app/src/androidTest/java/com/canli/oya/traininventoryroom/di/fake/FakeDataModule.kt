package com.canli.oya.traininventoryroom.di.fake

import com.canli.oya.traininventoryroom.data.BrandEntity
import com.canli.oya.traininventoryroom.data.CategoryEntity
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource
import com.canli.oya.traininventoryroom.datasource.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.di.TestScope
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock

@Module
class FakeDataModule {

    @Provides
    @TestScope
    fun provideTrainDataSource() : ITrainDataSource = FakeTrainDataSource()

    @Provides
    @TestScope
    fun provideCategoryDataSource() : IBrandCategoryDataSource<CategoryEntity> = FakeCategoryDataSource()

    @Provides
    @TestScope
    fun provideBrandDataSource() : IBrandCategoryDataSource<BrandEntity> = FakeBrandDataSource()

    @Provides
    @TestScope
    fun provideDatabase() : TrainDatabase = mock(TrainDatabase::class.java)
}