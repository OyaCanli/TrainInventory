package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.source.IBrandCategoryDataSource
import com.canli.oya.traininventoryroom.data.source.ITrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.AddBrandFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.AddCategoryFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.filter.FilterTrainFragment
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import com.canli.oya.traininventoryroom.ui.trash.TrashListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataSourceModule::class])
interface AppComponent {

    fun exposeTrainDataSource() : ITrainDataSource

    fun exposeBrandDataSource() : IBrandCategoryDataSource<BrandEntry>

    fun exposeCategoryDataSource() : IBrandCategoryDataSource<CategoryEntry>

    fun exposeDatabase() : TrainDatabase

    fun inject(target: MainActivity)
    fun inject(target: CategoryListFragment)
    fun inject(target: BrandListFragment)
    fun inject(target: TrainListFragment)
    fun inject(target: AddCategoryFragment)
    fun inject(target: AddBrandFragment)
    fun inject(target: TrainDetailsFragment)
    fun inject(target: FilterTrainFragment)
    fun inject(target: TrashListFragment)
}