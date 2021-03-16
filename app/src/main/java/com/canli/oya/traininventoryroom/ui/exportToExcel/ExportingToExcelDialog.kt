package com.canli.oya.traininventoryroom.ui.exportToExcel


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ExportToExcelBinding
import com.canli.oya.traininventoryroom.utils.EXCEL_FILE_PATH
import com.canli.oya.traininventoryroom.utils.shortToast
import timber.log.Timber


// Request code for creating an excel document.
const val CREATE_FILE_REQUEST = 492


class ExportingToExcelDialog : DialogFragment() {

    private lateinit var binding : ExportToExcelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_exporting_to_excel, container, false)

        launchCreateFileIntent()

        return binding.root
    }

    private fun launchCreateFileIntent() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
            putExtra(Intent.EXTRA_TITLE, "train_inventory.xls")

        }
        startActivityForResult(intent, CREATE_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CREATE_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            val fileUri = data?.data
            fileUri?.let {
                exportToExcelFile(it)
            }
        }
    }

    private fun exportToExcelFile(uri : Uri) {
        val dbToExcel = DbToExcel(requireContext(), "train_inventory", uri)
        val listOfTables = listOf("brands", "categories", "trains")
        dbToExcel.exportSpecificTables(
            listOfTables,
            object : DbToExcel.ExportListener {
                override fun onStart() {}

                override fun onCompleted() {
                    binding.lottieExportingAnim.cancelAnimation()
                    dismiss()

                    val uploadExcelDialog = UploadExcelDialog()
                    val args = Bundle()
                    args.putString(EXCEL_FILE_PATH, uri.toString())
                    uploadExcelDialog.arguments = args
                    parentFragmentManager.let {
                        uploadExcelDialog.show(it, null)
                    }
                }

                override fun onError(e: Exception?) {
                    Timber.e("${e?.printStackTrace()}")
                    context?.shortToast("Error while exporting")
                    binding.lottieExportingAnim.cancelAnimation()
                    dismiss()
                }
            })
    }
}