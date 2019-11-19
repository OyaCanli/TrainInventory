package com.canli.oya.traininventoryroom.di

import com.canli.oya.traininventoryroom.data.BrandDataSource
import com.canli.oya.traininventoryroom.data.CategoryDataSource
import com.canli.oya.traininventoryroom.data.TrainDataSource
import com.canli.oya.traininventoryroom.ui.brands.AddBrandFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.AddCategoryFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun exposeTrainDataSource() : TrainDataSource

    fun exposeBrandDataSource() : BrandDataSource

    fun exposeCategoryDataSource() : CategoryDataSource

    fun inject(target: CategoryListFragment)
    fun inject(target: BrandListFragment)
    fun inject(target: TrainListFragment)
    fun inject(target: AddCategoryFragment)
    fun inject(target: AddBrandFragment)
    fun inject(target: TrainDetailsFragment)
}