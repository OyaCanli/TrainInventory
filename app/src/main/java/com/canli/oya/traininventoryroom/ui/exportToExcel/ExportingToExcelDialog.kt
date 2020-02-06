package com.canli.oya.traininventoryroom.ui.exportToExcel


import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.ajts.androidmads.library.SQLiteToExcel
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ExportToExcelBinding
import com.canli.oya.traininventoryroom.utils.EXCEL_FILE_PATH
import com.canli.oya.traininventoryroom.utils.shortToast
import timber.log.Timber
import java.io.File


class ExportingToExcelDialog : DialogFragment() {

    private lateinit var binding : ExportToExcelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_exporting_to_excel, container, false)

        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator

        val sqliteToExcel = SQLiteToExcel(context, "train_inventory", directoryPath)
        sqliteToExcel.exportAllTables("train_inventory.xls", object : SQLiteToExcel.ExportListener {
            override fun onStart() {}

            override fun onCompleted(filePath: String) {

                binding.lottieExportingAnim.cancelAnimation()
                dismiss()

                val uploadExcelDialog = UploadExcelDialog()
                val args = Bundle()
                args.putString(EXCEL_FILE_PATH, filePath)
                uploadExcelDialog.arguments = args
                parentFragmentManager.let {
                    uploadExcelDialog.show(it, null)
                }
            }

            override fun onError(e: Exception) {
                context?.shortToast("Error while exporting")
                Timber.e(e.message)
                binding.lottieExportingAnim.cancelAnimation()
                dismiss()
            }
        })

        return binding.root
    }
}