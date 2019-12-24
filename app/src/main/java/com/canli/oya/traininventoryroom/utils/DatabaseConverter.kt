package com.canli.oya.traininventoryroom.utils

import android.content.Context
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import org.jetbrains.anko.toast
import timber.log.Timber


class DatabaseConverter {

    companion object {
        fun exportDatabaseToExcel(context : Context){
            val sqliteToExcel = SQLiteToExcel(context, "train_inventory")
            sqliteToExcel.exportAllTables("train_inventory.xls", object : ExportListener {
                override fun onStart() {
                    context.toast("exporting started")
                }
                override fun onCompleted(filePath: String) {
                    context.toast("Successfully exported")
                    Timber.d(filePath)
                }

                override fun onError(e: Exception) {
                    context.toast("Error while exporting")
                    Timber.e(e.message)
                }
            })
        }
    }


}