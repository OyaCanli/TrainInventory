package com.canli.oya.traininventoryroom.utils

import android.content.Context
import android.content.Intent
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
                    launchChooser(context, filePath)
                }

                override fun onError(e: Exception) {
                    context.toast("Error while exporting")
                    Timber.e(e.message)
                }
            })
        }

        fun launchChooser(context: Context, filePath : String){
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.apply {
                type = "text/xml"
                putExtra(Intent.EXTRA_SUBJECT, "inventory backup")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, filePath)
            }

            context.startActivity(Intent.createChooser(fileIntent, "Send file to:"))
        }
    }


}