package com.canli.oya.traininventoryroom.di

import android.app.Application
import com.canli.oya.traininventoryroom.di.AppComponent
import com.canli.oya.traininventoryroom.di.AppModule
import com.canli.oya.traininventoryroom.di.DaggerAppComponent
import com.facebook.drawee.backends.pipeline.Fresco
import timber.log.Timber

open class TrainApplication: Application() {

    open lateinit var appComponent : AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = initAppComponent()

        initLibraries()
    }

    open fun initAppComponent() : AppComponent{
        return DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    open fun initLibraries() {
        Timber.plant(Timber.DebugTree())
        Fresco.initialize(this)
    }
}