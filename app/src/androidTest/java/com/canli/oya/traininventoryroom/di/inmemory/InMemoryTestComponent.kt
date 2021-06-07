package com.canli.oya.traininventoryroom.di.inmemory

import com.canli.oya.traininventoryroom.data.BrandEntity
import com.canli.oya.traininventoryroom.data.CategoryEntity
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.di.AppComponent
import com.canli.oya.traininventoryroom.di.DataSourceModule
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TestScope
import com.canli.oya.traininventoryroom.endtoendtests.BrandTests
import com.canli.oya.traininventoryroom.endtoendtests.CategoryTests
import com.canli.oya.traininventoryroom.endtoendtests.NavigationTests
import com.canli.oya.traininventoryroom.endtoendtests.TrainTests
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@TestScope
@Component(modules = [TestAppModule::class, InMemoryDataModule::class, DataSourceModule::class])
interface InMemoryTestComponent : AppComponent {

    override fun exposeTrainDataSource() : ITrainDataSource
    override fun exposeBrandDataSource() : IBrandCategoryDataSource<BrandEntity>
    override fun exposeCategoryDataSource() : IBrandCategoryDataSource<CategoryEntity>

    fun inject(target: CategoryTests)
    fun inject(target: BrandTests)
    fun inject(target: TrainTests)
    fun inject(target: NavigationTests)
}