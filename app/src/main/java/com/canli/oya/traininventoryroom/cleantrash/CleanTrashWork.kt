package com.canli.oya.traininventoryroom.cleantrash

import android.content.Context
import android.database.SQLException
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.canli.oya.traininventoryroom.data.TrainDatabase
import java.time.LocalDate


class CleanTrashWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now().toEpochDay()
        val dateLimit = today.minus(30)

        return try {
            TrainDatabase.getInstance(applicationContext).trainDao().cleanOldItemsInTrash(dateLimit)
            Result.success()
        } catch (e: SQLException) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "cleanTrashTask"
    }

}
