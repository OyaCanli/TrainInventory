package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragmentTest
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragmentTest
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragmentTest
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragmentTest
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [TestAppModule::class, TestDataModule::class])
interface TestComponent : AppComponent {

    override fun exposeTrainDataSource() : ITrainDataSource
    override fun exposeBrandDataSource() : IBrandCategoryDataSource<BrandEntry>
    override fun exposeCategoryDataSource() : IBrandCategoryDataSource<CategoryEntry>

    fun inject(target: CategoryListFragmentTest)
    fun inject(target: BrandListFragmentTest)
    fun inject(target: TrainListFragmentTest)
    fun inject(target: TrainDetailsFragmentTest)
}