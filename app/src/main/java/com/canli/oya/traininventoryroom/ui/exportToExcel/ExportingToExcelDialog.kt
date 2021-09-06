package com.canli.oya.traininventoryroom.ui.exportToExcel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.ExportToExcelBinding
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.shortToast
import com.canlioya.databasetoexcel.DbToExcel
import timber.log.Timber

// Request code for creating an excel document.
const val CREATE_FILE_REQUEST = 492

class ExportingToExcelDialog : DialogFragment() {

    private lateinit var binding: ExportToExcelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_exporting_to_excel, container, false
        )

        when (Build.VERSION.SDK_INT) {
            29, 30 -> launchCreateFileIntent()
            in 23..28 -> checkWritePermission()
            in 21..23 -> launchCreateFileIntent()
        }

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

    private fun exportToExcelFile(uri: Uri) {
        val dbToExcel =
            DbToExcel(requireContext(), "train_inventory", uri)
        val listOfTables = listOf("brands", "categories", "trains")
        dbToExcel.exportSpecificTables(
            listOfTables,
            object : DbToExcel.ExportListener {
                override fun onStart() {}

                override fun onCompleted() {
                    binding.lottieExportingAnim.cancelAnimation()
                    dismiss()

                    val action = ExportingToExcelDialogDirections.actionExportToExcelToUploadExcelDialog(uri.toString())
                    activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)
                }

                override fun onError(e: Exception?) {
                    Timber.e("${e?.printStackTrace()}")
                    context?.shortToast("Error while exporting")
                    binding.lottieExportingAnim.cancelAnimation()
                    dismiss()
                }
            }
        )
    }

    private fun checkWritePermission() {
        if (needsPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MainActivity.REQUEST_STORAGE_PERMISSION
            )
        } else {
            launchCreateFileIntent()
        }
    }

    // Check whether permission is already given or not
    private fun needsPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
