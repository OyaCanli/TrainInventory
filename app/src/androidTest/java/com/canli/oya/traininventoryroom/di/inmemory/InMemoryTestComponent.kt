package com.canli.oya.traininventoryroom.di.inmemory

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.di.AppComponent
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TestScope
import com.canli.oya.traininventoryroom.endtoendtests.CategoryTests
import com.canli.oya.traininventoryroom.endtoendtests.NavigationTests
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@TestScope
@Component(modules = [TestAppModule::class, InMemoryDataModule::class, DataSourceModule::class])
interface InMemoryTestComponent : AppComponent {

    override fun exposeTrainDataSource() : ITrainDataSource
    override fun exposeBrandDataSource() : IBrandCategoryDataSource<BrandEntry>
    override fun exposeCategoryDataSource() : IBrandCategoryDataSource<CategoryEntry>

    fun inject(target: CategoryTests)
    fun inject(target: NavigationTests)
}