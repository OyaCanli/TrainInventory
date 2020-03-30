package com.canli.oya.traininventoryroom.di.fake

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.datasource.FakeBrandDataSource
import com.canli.oya.traininventoryroom.datasource.FakeCategoryDataSource
import com.canli.oya.traininventoryroom.datasource.FakeTrainDataSource
import com.canli.oya.traininventoryroom.di.TestScope
import com.canli.oya.traininventoryroom.ui.main.Navigator
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
    fun provideCategoryDataSource() : IBrandCategoryDataSource<CategoryEntry> = FakeCategoryDataSource()

    @Provides
    @TestScope
    fun provideBrandDataSource() : IBrandCategoryDataSource<BrandEntry> = FakeBrandDataSource()

    @Provides
    @TestScope
    fun provideDatabase() : TrainDatabase = mock(TrainDatabase::class.java)

    @TestScope
    @Provides
    fun provideNavigator() : Navigator = mock(Navigator::class.java)
}