package com.canlioya.databasetoexcel

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.*
import java.io.FileOutputStream
import java.util.*

/** Credits : Used a large chunk of code from the library https://github.com/androidmads/SQLite2XL.
I couldn't directly use the library because it was not updated for changes in the Android file system */

class DbToExcel(context: Context, dbName: String, uri : Uri) {

    private var database: SQLiteDatabase? = null

    private var mExcludeColumns: List<String>? = null

    private val fileDescriptor = context.contentResolver?.openFileDescriptor(uri, "w")?.fileDescriptor

    init {
        try {
            database = SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath(dbName).absolutePath,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAllTables(): ArrayList<String> {
        val tables = ArrayList<String>()
        val cursor = database!!.rawQuery(
            "select name from sqlite_master where type='table' order by name",
            null
        )
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0))
        }
        cursor.close()
        return tables
    }

    private fun getColumns(table: String): ArrayList<String> {
        val columns = ArrayList<String>()
        val cursor = database!!.rawQuery("PRAGMA table_info($table)", null)
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(1))
        }
        cursor.close()
        return columns
    }

    @Throws(Exception::class)
    private fun exportTables(tables: List<String>){
        val workbook = HSSFWorkbook()
        for (i in tables.indices) {
            if (tables[i] != "android_metadata") {
                val sheet = workbook.createSheet(tables[i])
                createSheet(tables[i], sheet)
            }
        }
        val fos = FileOutputStream(fileDescriptor)
        workbook.write(fos)
        fos.flush()
        fos.close()
        workbook.close()
        database?.close()
    }

    fun exportSpecificTables(tables: List<String>, listener: ExportListener?) {
        startExportTables(tables, listener)
    }

    fun exportAllTables(listener: ExportListener?) {
        val tables = getAllTables()
        startExportTables(tables, listener)
    }

    private fun startExportTables(tables: List<String>, listener: ExportListener?) {
        listener?.onStart()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                exportTables(tables)
                withContext(Dispatchers.Main){
                    listener?.onCompleted()
                }
            } catch (e: Exception) {
                if (database?.isOpen == true) {
                    database?.close()
                }
                withContext(Dispatchers.Main){
                    listener?.onError(e)
                }
            }
        }
    }

    private fun createSheet(table: String, sheet: HSSFSheet) {
        val rowA = sheet.createRow(0)
        val columns = getColumns(table)
        var cellIndex = 0
        for (i in columns.indices) {
            val columnName = columns[i]
            if (!excludeColumn(columnName)) {
                val cellA = rowA.createCell(cellIndex)
                cellA.setCellValue(HSSFRichTextString(columnName))
                cellIndex++
            }
        }
        insertItemToSheet(table, sheet, columns)
    }

    private fun insertItemToSheet(table: String, sheet: HSSFSheet, columns: ArrayList<String>) {
        val cursor = database?.rawQuery("select * from $table", null)
        cursor?.moveToFirst()
        var n = 1
        while (cursor?.isAfterLast == false) {
            val rowA = sheet.createRow(n)
            var cellIndex = 0
            for (j in columns.indices) {
                val columnName = columns[j]
                if (!excludeColumn(columnName)) {
                    val cellA = rowA.createCell(cellIndex)
                    if (cursor.getType(j) != Cursor.FIELD_TYPE_BLOB) {
                        cellA.setCellValue(HSSFRichTextString(cursor.getString(j)))
                    }
                    cellIndex++
                }
            }
            n++
            cursor.moveToNext()
        }
        cursor?.close()
    }

    private fun excludeColumn(column: String?): Boolean {
        return mExcludeColumns?.contains(column) ?: false
    }

    interface ExportListener {
        fun onStart()
        fun onCompleted()
        fun onError(e: Exception?)
    }

}