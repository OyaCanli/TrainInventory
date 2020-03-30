package com.canli.oya.traininventoryroom.di

import android.app.Application
import android.content.Context
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.ui.main.Navigator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Singleton
    @Provides
    fun provideNavigator() : Navigator = Navigator()

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = TrainDatabase.getInstance(context)

}