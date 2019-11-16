package com.canli.oya.traininventoryroom.common

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import timber.log.Timber

open class TrainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    open fun init() {
        Timber.plant(Timber.DebugTree())
        Fresco.initialize(this)
    }
}