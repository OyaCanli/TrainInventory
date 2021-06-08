package com.canli.oya.traininventoryroom.di

import android.app.Application
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.canli.oya.traininventoryroom.cleantrash.CleanTrashWork
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TrainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        initLibraries()

        GlobalScope.launch {
            setUpTrashCleaningJob()
        }
    }

    private fun setUpTrashCleaningJob() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<CleanTrashWork>(7, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            CleanTrashWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

    open fun initLibraries() {
        Timber.plant(Timber.DebugTree())
        Fresco.initialize(this)
    }
}