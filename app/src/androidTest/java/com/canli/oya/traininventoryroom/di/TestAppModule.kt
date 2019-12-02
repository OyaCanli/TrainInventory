package com.canli.oya.traininventoryroom.di

import android.app.Application
import android.content.Context
import com.canli.oya.traininventoryroom.data.TrainDatabase
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

@Module
class TestAppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }

    @Singleton
    @Provides
    fun provideDatabase() : TrainDatabase {
        return mock(TrainDatabase::class.java)
    }

}