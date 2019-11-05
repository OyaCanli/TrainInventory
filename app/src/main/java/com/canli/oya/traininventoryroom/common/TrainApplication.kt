package com.canli.oya.traininventoryroom.common

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import timber.log.Timber

class TrainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Fresco.initialize(this)
    }
}