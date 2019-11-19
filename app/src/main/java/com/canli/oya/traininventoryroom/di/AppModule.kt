package com.canli.oya.traininventoryroom.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.canli.oya.traininventoryroom.data.TrainDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }

    @Provides
    @Singleton
    fun provideResources(): Resources {
        return app.resources
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context) : TrainDatabase {
        return TrainDatabase.getInstance(context)
    }
}