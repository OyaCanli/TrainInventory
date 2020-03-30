package com.canli.oya.traininventoryroom.di

import android.app.Application

class ComponentProvider private constructor(app: Application) {

    var daggerComponent = DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

    companion object {

        @Volatile private var sInstance: ComponentProvider? = null

        fun getInstance(context: Application): ComponentProvider {
            return sInstance ?: synchronized(this) {
                sInstance ?: ComponentProvider(context)
                        .also { sInstance = it }
            }
        }
    }

}