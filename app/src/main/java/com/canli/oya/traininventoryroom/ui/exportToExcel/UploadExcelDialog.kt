package com.canli.oya.traininventoryroom.ui.exportToExcel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.UploadExcelBinding
import com.canli.oya.traininventoryroom.utils.EXCEL_FILE_PATH
import com.canli.oya.traininventoryroom.utils.PROVIDER_AUTHORITY
import org.jetbrains.anko.toast
import java.io.File


class UploadExcelDialog : DialogFragment() {

    private lateinit var binding: UploadExcelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_upload_excel, container, false)

        val filePath = arguments?.getString(EXCEL_FILE_PATH)

        if (filePath.isNullOrBlank()) {
            context?.toast("File path was not properly received")
        } else {
            val newFile = File(filePath)
            val contentUri: Uri = FileProvider.getUriForFile(context!!, PROVIDER_AUTHORITY, newFile)

            binding.uploadBtn.setOnClickListener {
                launchChooser(contentUri)
            }

            binding.launchExcelBtn.setOnClickListener {
                openExcelFile(contentUri)
            }
        }

        return binding.root
    }

    private fun launchChooser(fileUri: Uri) {
        val fileIntent = Intent(Intent.ACTION_SEND)
        fileIntent.apply {
            type = "application/vnd.ms-excel"
            putExtra(Intent.EXTRA_SUBJECT, "train inventory backup")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }

        context?.startActivity(Intent.createChooser(fileIntent, getString(R.string.uopload_file_to)))
    }

    private fun openExcelFile(fileUri: Uri) {
        val openFileIntent = Intent(Intent.ACTION_VIEW)
        openFileIntent.apply {
            setDataAndType(fileUri, "application/vnd.ms-excel")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context?.startActivity(Intent.createChooser(openFileIntent, getString(R.string.open_file_with)))
    }
}