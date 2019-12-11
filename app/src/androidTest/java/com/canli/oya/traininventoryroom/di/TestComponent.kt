package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.source.IBrandDataSource
import com.canli.oya.traininventoryroom.data.source.ICategoryDataSource
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
    override fun exposeBrandDataSource() : IBrandDataSource
    override fun exposeCategoryDataSource() : ICategoryDataSource

    fun inject(target: CategoryListFragmentTest)
    fun inject(target: BrandListFragmentTest)
    fun inject(target: TrainListFragmentTest)
    fun inject(target: TrainDetailsFragmentTest)
}