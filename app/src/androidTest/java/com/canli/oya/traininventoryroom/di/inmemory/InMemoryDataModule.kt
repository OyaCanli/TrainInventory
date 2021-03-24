package com.canli.oya.traininventoryroom.di.inmemory

import androidx.navigation.NavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.di.TestScope
import dagger.Module
import dagger.Provides

@Module
class InMemoryDataModule {

    @Provides
    @TestScope
    fun provideDatabase() : TrainDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TrainDatabase::class.java
    ).build()

}