package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.datasource.IBrandDataSource
import com.canli.oya.traininventoryroom.data.datasource.ICategoryDataSource
import com.canli.oya.traininventoryroom.data.datasource.ITrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.AddBrandFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.AddCategoryFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataSourceModule::class])
interface AppComponent {

    fun exposeTrainDataSource() : ITrainDataSource

    fun exposeBrandDataSource() : IBrandDataSource

    fun exposeCategoryDataSource() : ICategoryDataSource

    fun exposeDatabase() : TrainDatabase

    fun inject(target: CategoryListFragment)
    fun inject(target: BrandListFragment)
    fun inject(target: TrainListFragment)
    fun inject(target: AddCategoryFragment)
    fun inject(target: AddBrandFragment)
    fun inject(target: TrainDetailsFragment)
}