package com.canli.oya.traininventoryroom.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.CustomSpinAdapter
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddTrainBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.AddTrainFactory
import com.canli.oya.traininventoryroom.viewmodel.AddTrainViewModel
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException

class AddTrainFragment : Fragment(), View.OnClickListener{

    private lateinit var binding: FragmentAddTrainBinding

    private val mainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private lateinit var addViewModel: AddTrainViewModel

    private var mTempPhotoPath: String? = null
    private var mImageUri: String? = null
    private var mUsersChoice: Int = 0

    private var isEdit: Boolean = false

    var chosenTrain : TrainEntry? = null

    private val mDialogClickListener = DialogInterface.OnClickListener { _, item -> mUsersChoice = item }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_train, container, false)

        setHasOptionsMenu(true)

        //Set click listener on buttons
        binding.addTrainAddBrandBtn.setOnClickListener(this)
        binding.addTrainAddCategoryBtn.setOnClickListener(this)
        binding.productDetailsGalleryImage.setOnClickListener(this)

        activity?.title = if(isEdit) getString(R.string.edit_train)
                            else getString(R.string.add_train)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        chosenTrain = arguments?.getParcelable(CHOSEN_TRAIN)

        val factory = AddTrainFactory(requireActivity().application, chosenTrain)
        addViewModel = ViewModelProviders.of(this, factory).get(AddTrainViewModel::class.java)
        binding.viewModel = addViewModel

        setBrandSpinner()
        setCategorySpinner()
    }

    private fun setCategorySpinner() {
        val categoryList = ArrayList<String>()
        categoryList.add("--Select category--")
        val categoryAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, categoryList)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter
        mainViewModel.categoryList?.observe(this@AddTrainFragment, Observer { categoryEntries ->
            if (!categoryEntries.isNullOrEmpty()) {
                categoryList.clear()
                categoryList.add("--Select category--")
                categoryList.addAll(categoryEntries)
                categoryAdapter.notifyDataSetChanged()
                addViewModel.categoryList = categoryList
                val index = categoryList.indexOf(chosenTrain?.categoryName)
                binding.categorySpinner.setSelection(index)
            }
        })
    }

    private fun setBrandSpinner() {
        val brandAdapter = CustomSpinAdapter(requireContext(), null)
        binding.brandSpinner.adapter = brandAdapter
        mainViewModel.brandList?.observe(this@AddTrainFragment, Observer { brandEntries ->
            if (!brandEntries.isNullOrEmpty()) {
                brandAdapter.mBrandList = brandEntries
                brandAdapter.notifyDataSetChanged()
                addViewModel.brandList = brandEntries
                val index = brandEntries.indexOfFirst { it.brandName == chosenTrain?.brandName }.plus(1)
                binding.brandSpinner.setSelection(index)
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addTrain_addBrandBtn -> insertAddBrandFragment()
            R.id.addTrain_addCategoryBtn -> insertAddCategoryFragment()
            R.id.product_details_gallery_image -> openImageDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_with_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> saveTrain()
            android.R.id.home -> activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertAddCategoryFragment() {
        childFragmentManager.transaction {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.childFragContainer, AddCategoryFragment())
        }
    }

    private fun insertAddBrandFragment() {
        childFragmentManager.transaction {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.childFragContainer, AddBrandFragment())
        }
    }

    private fun saveTrain() {

        // DATA VALIDATION
        if (!quantityIsValid()) return
        if (trainNameIsEmpty()) return
        if (brandNameIsEmpty()) return
        if (categoryNameIsEmpty()) return
        if (scaleIsEmpty()) return

        // SAVE
        addViewModel.saveTrain()

        //After adding the train, go back to where user come from.
        fragmentManager?.popBackStack()
    }

    private fun scaleIsEmpty(): Boolean {
        if (addViewModel.trainBeingModified.get()?.scale == null) {
            context?.toast("Scale cannot be empty")
            return true
        }
        return false
    }

    private fun brandNameIsEmpty() : Boolean {
        if(addViewModel.trainBeingModified.get()?.brandName == null){
            context?.toast("Brand name cannot be empty")
            return true
        }
        return false
    }

    private fun categoryNameIsEmpty() : Boolean {
        if(addViewModel.trainBeingModified.get()?.categoryName == null){
            context?.toast("Category name cannot be empty")
            return true
        }
        return false
    }

    private fun trainNameIsEmpty(): Boolean {
        if (addViewModel.trainBeingModified.get()?.trainName == null) {
            context?.toast("Train name cannot be empty")
            return true
        }
        return false
    }

    private fun quantityIsValid(): Boolean {
        val quantityToParse = binding.editQuantity.text.toString().trim()
        //Quantity can be null. But if it is not null it should be a positive integer
        val quantity: Int
        if (!TextUtils.isEmpty(quantityToParse)) {
            try {
                quantity = Integer.valueOf(quantityToParse)
                if (quantity < 0) {
                    context?.toast(R.string.quantity_should_be_positive)
                    return false
                } else {
                    addViewModel.trainBeingModified.get()?.quantity = quantity
                }
            } catch (nfe: NumberFormatException) {
                context?.toast(R.string.quantity_should_be_positive)
                return false
            }
        }
        return true
    }

    private fun openImageDialog() {
        val dialogOptions = activity!!.resources.getStringArray(R.array.dialog_options)
        val builder = AlertDialog.Builder(requireContext(), R.style.alert_dialog_style)
        with(builder) {
            setTitle(R.string.add_image_from)
            setSingleChoiceItems(dialogOptions, -1, mDialogClickListener)
            setPositiveButton(R.string.ok) { _, _ ->
                when (mUsersChoice) {
                    0 -> if (needsPermission()) requestPermission()
                    else openCamera()
                    1 -> openGallery()
                }
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
            create()
            show()
        }
    }

    //Check whether permission is already given or not
    private fun needsPermission() = ContextCompat.checkSelfPermission(activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

    // If you do not have permission, request it
    private fun requestPermission() = this@AddTrainFragment.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION)

    private fun openGallery() {
        val intent = Intent()
        // Show only images, no videos or anything else
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
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

                val imageUri = FileProvider.getUriForFile(activity!!,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile)
                mImageUri = imageUri.toString()
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                addViewModel.trainBeingModified.get()?.imageUri = mImageUri
                binding.invalidateAll()
            } else {
                BitmapUtils.deleteImageFile(activity!!, mTempPhotoPath!!)
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                mImageUri = imageUri?.toString()
                addViewModel.trainBeingModified.get()?.imageUri = mImageUri
                binding.invalidateAll()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Called when you request permission to read and write to external storage
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    openCamera()
                } else {
                    // If you do not get permission, show a Toast
                    context?.toast(getString(R.string.permission_denied))
                }
            }
        }
    }

    fun onBackClicked(){
        if(addViewModel.isChanged){
            showUnsavedChangesDialog()
        } else {
            fragmentManager?.popBackStack()
        }
    }

    private fun showUnsavedChangesDialog() {
        //If user clicks back when there are unsaved changes in AddTrainFragment, warn user with a dialog.
        val builder = AlertDialog.Builder(requireContext(), R.style.alert_dialog_style)
        with(builder) {
            setMessage(R.string.unsaved_changes_warning)
            setPositiveButton(getString(R.string.discard_changes)) { _, _ ->
                //Changes will be discarded
                fragmentManager?.popBackStack()
            }
            setNegativeButton(R.string.keep_editing) { dialog, _ ->
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing
                dialog?.dismiss()
            }
            create()
            show()
        }
    }
}
