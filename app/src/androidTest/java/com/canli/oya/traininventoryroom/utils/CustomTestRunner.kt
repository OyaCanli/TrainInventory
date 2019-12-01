package com.canli.oya.traininventoryroom.utils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, "com.canli.oya.traininventoryroom.di.AndroidTestApplication", context)
    }
}