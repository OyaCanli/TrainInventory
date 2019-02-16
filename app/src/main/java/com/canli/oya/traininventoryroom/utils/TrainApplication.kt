package com.canli.oya.traininventoryroom.utils

import android.app.Application
import timber.log.Timber

class TrainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}