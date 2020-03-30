package com.canli.oya.traininventoryroom.di.fake

import com.canli.oya.traininventoryroom.di.AppComponent
import com.canli.oya.traininventoryroom.di.TestAppModule
import com.canli.oya.traininventoryroom.di.TestScope
import com.canli.oya.traininventoryroom.fragmenttests.*
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@TestScope
@Component(modules = [TestAppModule::class, FakeDataModule::class])
interface FakeTestComponent : AppComponent {

    fun inject(target: CategoryListFragmentTest)
    fun inject(target: BrandListFragmentTest)
    fun inject(target: TrainListFragmentTest)
    fun inject(target: TrainDetailsFragmentTest)
    fun inject(target : EmptyScreenTests)
}
