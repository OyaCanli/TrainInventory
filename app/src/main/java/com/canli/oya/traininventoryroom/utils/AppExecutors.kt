package com.canli.oya.traininventoryroom.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(private val diskIO: Executor) {

    fun diskIO(): Executor {
        return diskIO
    }

    companion object {

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: AppExecutors? = null

        val instance: AppExecutors
            get() {
                if (sInstance == null) {
                    synchronized(LOCK) {
                        sInstance = AppExecutors(Executors.newSingleThreadExecutor())
                    }
                }
                return sInstance!!
            }
    }
}

