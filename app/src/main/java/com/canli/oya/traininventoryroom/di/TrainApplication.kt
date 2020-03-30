package com.canli.oya.traininventoryroom.di

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import timber.log.Timber

open class TrainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        initLibraries()
    }

    open fun initLibraries() {
        Timber.plant(Timber.DebugTree())
        Fresco.initialize(this)
    }
}