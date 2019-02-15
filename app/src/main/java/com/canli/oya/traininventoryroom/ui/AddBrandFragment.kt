package com.canli.oya.traininventoryroom.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddBrandBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import java.io.File
import java.io.IOException

class AddBrandFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentAddBrandBinding

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private var mContext: Context? = null
    private var mBrandId: Int = 0

    private var pickImageDialog: AlertDialog? = null
    private var mTempPhotoPath: String? = null
    private var mLogoUri: Uri? = null
    private var mUsersChoice: Int = 0
    private var isUpdateCase: Boolean = false

    private val mDialogClickListener = DialogInterface.OnClickListener { _, item -> mUsersChoice = item }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_brand, container, false)

        //Set click listeners
        binding.addBrandSaveBtn.setOnClickListener(this)
        binding.addBrandImage.setOnClickListener(this)

        //Request focus on the first edit text
        binding.addBrandEditBrandName.requestFocus()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments?.containsKey(INTENT_REQUEST_CODE) == true) { //This is the "edit" case
            isUpdateCase = true
            mViewModel.chosenBrand.observe(this@AddBrandFragment, Observer { brandEntry ->
                brandEntry?.let { populateFields(it)
                        .also { mBrandId = brandEntry.brandId }}
            })
        }
    }

    private fun populateFields(brand: BrandEntry?) {
        binding.chosenBrand = brand
        binding.isEdit = true
    }

    override fun onClick(v: View) {
        //If save is clicked
        if (v.id == R.id.addBrand_saveBtn) {
            saveBrand()
        } else {
            //If add photo is clicked
            openImageDialog()
        }
    }

    private fun saveBrand() {
        //Get brand name from edit text
        val brandName = binding.addBrandEditBrandName.text.toString().trim { it <= ' ' }

        //Get web address from edit text
        val webAddress = binding.addBrandEditWeb.text.toString().trim { it <= ' ' }

        //If there is a uri for logo image, parse it to string
        var imagePath: String? = null
        if (mLogoUri != null) {
            imagePath = mLogoUri.toString()
        }

        if (isUpdateCase) {
            //Construct a new BrandEntry object from this data with ID included
            val brandToUpdate = BrandEntry(mBrandId, brandName, imagePath, webAddress)
            mViewModel.updateBrand(brandToUpdate)

        } else {
            //Construct a new BrandEntry object from this data (without ID)
            val newBrand = BrandEntry(brandName = brandName, brandLogoUri = imagePath, webUrl = webAddress)
            //Insert to database in a background thread
            mViewModel.insertBrand(newBrand)
        }

        Toast.makeText(activity, R.string.brand_Saved, Toast.LENGTH_SHORT).show()

        //Remove fragment
        val parentFrag = parentFragment
        val currentInstance: androidx.fragment.app.Fragment?
        if (parentFrag is AddTrainFragment) {
            currentInstance = fragmentManager?.findFragmentById(R.id.childFragContainer)
        } else {
            currentInstance = fragmentManager?.findFragmentById(R.id.brandlist_addFrag_container)
        }

        //
        val focusedView = activity!!.currentFocus
        if (focusedView != null) {
            focusedView.clearFocus()
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }

        fragmentManager!!.beginTransaction()
                .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(currentInstance!!)
                .commit()

    }

    private fun openImageDialog() {

        //Opens a dialog which lets the user choose either adding a photo from gallery or taking a new picture.
        val dialogOptions = activity!!.resources.getStringArray(R.array.dialog_options)
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.add_image_from)
        builder.setSingleChoiceItems(dialogOptions, -1, mDialogClickListener)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            when (mUsersChoice) {
                0 -> {
                    if (ContextCompat.checkSelfPermission(activity!!,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // If you do not have permission, request it
                        this@AddBrandFragment.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                REQUEST_STORAGE_PERMISSION)
                    } else {
                        // Launch the camera if the permission exists
                        openCamera()
                    }
                }
                1 -> openGallery()
            }
        }
        builder.setNegativeButton(R.string.cancel) { dialog, id -> }
        pickImageDialog = builder.create()
        pickImageDialog!!.show()
    }

    private fun openGallery() {
        val intent = Intent()
        // Show only images, no videos or anything else
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = BitmapUtils.createImageFile(activity!!)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.absolutePath

                mLogoUri = FileProvider.getUriForFile(activity!!,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLogoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pickImageDialog?.dismiss()
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Glide.with(mContext!!)
                        .load(mLogoUri)
                        .into(binding.addBrandImage)
            } else {
                BitmapUtils.deleteImageFile(mContext!!, mTempPhotoPath!!)
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mLogoUri = data?.data
                Glide.with(mContext!!)
                        .load(mLogoUri)
                        .into(binding.addBrandImage)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        // Called when you request permission to read and write to external storage
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    openCamera()
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    /*override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        return if (!enter && parentFragment is BrandListFragment) dummyAnimation
                else super.onCreateAnimation(transit, enter, nextAnim)
    }

    companion object {

        *//*This is for solving the weird behaviour of child fragments during exit.
    I found this solution from this SO entry and adapted to my case:
    https://stackoverflow.com/questions/14900738/nested-fragments-disappear-during-transition-animation*//*
        private val dummyAnimation = AlphaAnimation(1f, 1f)

        init {
            dummyAnimation.duration = 500
        }
    }*/
}
