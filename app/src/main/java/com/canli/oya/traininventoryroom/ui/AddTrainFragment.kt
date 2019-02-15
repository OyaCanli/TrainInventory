package com.canli.oya.traininventoryroom.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.CustomSpinAdapter
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainDatabase
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddTrainBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.ChosenTrainFactory
import com.canli.oya.traininventoryroom.viewmodel.ChosenTrainViewModel
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import java.io.File
import java.io.IOException

class AddTrainFragment : androidx.fragment.app.Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentAddTrainBinding

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private var mChosenCategory: String? = null
    private var mChosenBrand: String? = null
    private var pickImageDialog: AlertDialog? = null
    private var mTempPhotoPath: String? = null
    private var mImageUri: String? = null
    private var mUsersChoice: Int = 0
    private var mCategoryList: MutableList<String> = mutableListOf()
    private var mBrandList: MutableList<BrandEntry> = mutableListOf()
    private var mTrainId: Int = 0
    private var mCallback: UnsavedChangesListener? = null
    private var mChosenTrain: TrainEntry? = null
    private var isEdit: Boolean = false
    private var brandsLoaded: Boolean = false
    private var categoryLoaded: Boolean = false
    private var trainLoaded: Boolean = false
    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            mCallback?.warnForUnsavedChanges(true)
        }
    }

    private val mDialogClickListener = DialogInterface.OnClickListener { dialog, item -> mUsersChoice = item }

    interface UnsavedChangesListener {
        fun warnForUnsavedChanges(shouldWarn: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = context as UnsavedChangesListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement UnsavedChangesListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_train, container, false)

        setHasOptionsMenu(true)

        //Set click listener on buttons
        binding.addTrainAddBrandBtn.setOnClickListener(this)
        binding.addTrainAddCategoryBtn.setOnClickListener(this)
        binding.productDetailsGalleryImage.setOnClickListener(this)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val database = TrainDatabase.getInstance(activity!!.applicationContext)

        mViewModel.loadBrandList(InjectorUtils.provideBrandRepo(requireContext()))
        mViewModel.loadCategoryList(InjectorUtils.provideCategoryRepo(requireContext()))

        //Set brand spinner
        val brandAdapter = CustomSpinAdapter(activity!!, mBrandList)
        binding.brandSpinner.adapter = brandAdapter
        binding.brandSpinner.onItemSelectedListener = this
        mViewModel.brandList?.observe(this@AddTrainFragment, Observer { brandEntries ->
            if(!brandEntries.isNullOrEmpty()){
                mBrandList.clear()
                mBrandList.addAll(brandEntries)
                brandAdapter.notifyDataSetChanged()
                brandsLoaded = true
                setSpinners()
            }
        })

        //Set category spinner
        val categoryAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, mCategoryList!!)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter
        binding.categorySpinner.onItemSelectedListener = this
        mViewModel.categoryList?.observe(this@AddTrainFragment, Observer { categoryEntries ->
            if(!categoryEntries.isNullOrEmpty()){
                mCategoryList.clear()
                mCategoryList.addAll(categoryEntries)
                categoryAdapter.notifyDataSetChanged()
                categoryLoaded = true
                setSpinners()
            }
        })

        val bundle = arguments
        //"Edit" case
        if (bundle != null && bundle.containsKey(TRAIN_ID)) {
            activity!!.title = getString(R.string.edit_train)
            isEdit = true
            mTrainId = bundle.getInt(TRAIN_ID)
            //This view model is instantiated only in edit mode. It contains the chosen train. It is attached to this fragment
            val factory = ChosenTrainFactory(database, mTrainId)
            val viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel::class.java)
            viewModel.chosenTrain.observe(this, Observer { trainEntry ->
                binding.chosenTrain = trainEntry
                binding.executePendingBindings()
                mChosenTrain = trainEntry
                trainLoaded = true
                setSpinners()
                if (savedInstanceState != null) {
                    restoreState(savedInstanceState)
                }
            })

            setTouchListenersToEditTexts()
        } else { //This is the "add" case
            activity!!.title = getString(R.string.add_train)
            binding.executePendingBindings()
            isEdit = false
            setChangeListenersToEdittexts()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null && !isEdit) {
            restoreState(savedInstanceState)
        }
    }

    private fun restoreState(savedInstanceState: Bundle) {
        binding.brandSpinner.setSelection(savedInstanceState.getInt(BRAND_SPINNER_POSITION))
        binding.categorySpinner.setSelection(savedInstanceState.getInt(CATEGORY_SPINNER_POSITION))
        mImageUri = savedInstanceState.getString(IMAGE_URL)
        GlideApp.with(this@AddTrainFragment)
                .load(mImageUri)
                .placeholder(R.drawable.ic_gallery)
                .into(binding.productDetailsGalleryImage)
        binding.editTrainName.setText(savedInstanceState.getString(NAME_ET))
        binding.editTrainDescription.setText(savedInstanceState.getString(DESCRIPTION_ET))
        binding.editReference.setText(savedInstanceState.getString(MODEL_ET))
        binding.editQuantity.setText(savedInstanceState.getString(QUANTITY_ET))
        binding.editLocationLetter.setText(savedInstanceState.getString(LOCATION_LETTER_ET))
        binding.editLocationNumber.setText(savedInstanceState.getString(LOCATION_NUMBER_ET))
        binding.editScale.setText(savedInstanceState.getString(SCALE_ET))
    }

    private fun setSpinners() {
        if (trainLoaded && categoryLoaded && brandsLoaded) {
            //Set category spinner
            binding.categorySpinner.setSelection(mCategoryList.indexOf(mChosenTrain!!.categoryName))
            //Set brand spinner
            var brandIndex = 0
            for (i in mBrandList.indices) {
                if (mBrandList[i].brandName == mChosenTrain!!.brandName) {
                    brandIndex = i
                }
            }
            binding.brandSpinner.setSelection(brandIndex)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addTrain_addBrandBtn -> {
                insertAddBrandFragment()
            }
            R.id.addTrain_addCategoryBtn -> {
                insertAddCategoryFragment()
            }
            R.id.product_details_gallery_image -> {
                openImageDialog()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_with_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            saveTrain()
        } else if (id == android.R.id.home) {
            activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertAddCategoryFragment() {
        val addCatFrag = AddCategoryFragment()
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.childFragContainer, addCatFrag)
                .commit()
    }

    private fun insertAddBrandFragment() {
        val addBrandFrag = AddBrandFragment()
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.childFragContainer, addBrandFrag)
                .commit()
    }

    private fun saveTrain() {
        val quantityToParse = binding.editQuantity.text.toString().trim { it <= ' ' }
        //Quantity can be null. But if it is not null it should be a positive integer
        var quantity = 0
        if (!TextUtils.isEmpty(quantityToParse)) {
            try {
                quantity = Integer.valueOf(quantityToParse)
                if (quantity < 0) {
                    Toast.makeText(activity, R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show()
                    return
                }
            } catch (nfe: NumberFormatException) {
                Toast.makeText(activity, R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show()
                return
            }

        }
        val reference = binding.editReference.text.toString().trim { it <= ' ' }
        val trainName = binding.editTrainName.text.toString().trim { it <= ' ' }
        val description = binding.editTrainDescription.text.toString().trim { it <= ' ' }
        val location = binding.editLocationNumber.text.toString().trim { it <= ' ' } + "-" +
                binding.editLocationLetter.text.toString().trim { it <= ' ' }
        val scale = binding.editScale.text.toString().trim { it <= ' ' }

        if (mTrainId == 0) {
            //If this is a new train
            val newTrain = TrainEntry(trainName = trainName, modelReference = reference,
                    brandName = mChosenBrand, categoryName = mChosenCategory, quantity = quantity,
                    imageUri = mImageUri, description = description, location = location, scale = scale)
            mViewModel.insertTrain(newTrain)
        } else {
            //If this is a train that already exist
            val trainToUpdate = TrainEntry(mTrainId, trainName, reference, mChosenBrand, mChosenCategory, quantity, mImageUri, description, location, scale)
            mViewModel.updateTrain(trainToUpdate)
        }
        //After adding the train, go back to where user come from.
        mCallback!!.warnForUnsavedChanges(false)
        activity!!.onBackPressed()
    }

    private fun openImageDialog() {
        val dialogOptions = activity!!.resources.getStringArray(R.array.dialog_options)
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.add_image_from)
        builder.setSingleChoiceItems(dialogOptions, -1, mDialogClickListener)
        builder.setPositiveButton(R.string.ok) { dialog, which ->
            when (mUsersChoice) {
                0 -> {
                    if (ContextCompat.checkSelfPermission(activity!!,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // If you do not have permission, request it
                        this@AddTrainFragment.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
        pickImageDialog!!.dismiss()
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                GlideApp.with(this@AddTrainFragment)
                        .load(mImageUri)
                        .placeholder(R.drawable.ic_gallery)
                        .into(binding.productDetailsGalleryImage)
            } else {
                BitmapUtils.deleteImageFile(activity!!, mTempPhotoPath!!)
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                val imageUri = data!!.data
                mImageUri = imageUri!!.toString()
                GlideApp.with(this@AddTrainFragment)
                        .load(mImageUri)
                        .placeholder(R.drawable.ic_gallery)
                        .into(binding.productDetailsGalleryImage)
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
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemSelected(spinner: AdapterView<*>, view: View, position: Int, id: Long) {
        if (spinner.id == R.id.brandSpinner) {
            mChosenBrand = mBrandList[position].brandName
        } else {
            mChosenCategory = mCategoryList[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NAME_ET, binding.editTrainName.text.toString())
        outState.putString(DESCRIPTION_ET, binding.editTrainDescription.text.toString())
        outState.putString(MODEL_ET, binding.editReference.text.toString())
        outState.putString(QUANTITY_ET, binding.editQuantity.text.toString())
        outState.putString(SCALE_ET, binding.editScale.text.toString())
        outState.putString(LOCATION_NUMBER_ET, binding.editLocationNumber.text.toString())
        outState.putString(LOCATION_LETTER_ET, binding.editLocationLetter.text.toString())
        outState.putInt(BRAND_SPINNER_POSITION, binding.brandSpinner.selectedItemPosition)
        outState.putInt(CATEGORY_SPINNER_POSITION, binding.categorySpinner.selectedItemPosition)
        outState.putString(IMAGE_URL, mImageUri)
    }

    override fun onDetach() {
        super.onDetach()
        mCallback!!.warnForUnsavedChanges(false)
    }

    private fun setChangeListenersToEdittexts() {
        //Set change listeners on edit texts
        binding.editReference.addTextChangedListener(mTextWatcher)
        binding.editTrainName.addTextChangedListener(mTextWatcher)
        binding.editTrainDescription.addTextChangedListener(mTextWatcher)
        binding.editLocationNumber.addTextChangedListener(mTextWatcher)
        binding.editLocationLetter.addTextChangedListener(mTextWatcher)
        binding.editScale.addTextChangedListener(mTextWatcher)
        binding.editQuantity.addTextChangedListener(mTextWatcher)
    }

    private fun setTouchListenersToEditTexts() {
        val touchListener = View.OnTouchListener { v, event ->
            mCallback!!.warnForUnsavedChanges(true)
            false
        }

        //Set change listeners on edit texts
        binding.editReference.setOnTouchListener(touchListener)
        binding.editTrainName.setOnTouchListener(touchListener)
        binding.editTrainDescription.setOnTouchListener(touchListener)
        binding.editLocationNumber.setOnTouchListener(touchListener)
        binding.editLocationLetter.setOnTouchListener(touchListener)
        binding.editScale.setOnTouchListener(touchListener)
        binding.editQuantity.setOnTouchListener(touchListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isEdit) {
            removeTextWatchers()
        } else {
            removeTouchListeners()
        }
    }

    private fun removeTextWatchers() {
        binding.editReference.removeTextChangedListener(mTextWatcher)
        binding.editTrainName.removeTextChangedListener(mTextWatcher)
        binding.editTrainDescription.removeTextChangedListener(mTextWatcher)
        binding.editLocationNumber.removeTextChangedListener(mTextWatcher)
        binding.editLocationLetter.removeTextChangedListener(mTextWatcher)
        binding.editScale.removeTextChangedListener(mTextWatcher)
        binding.editQuantity.removeTextChangedListener(mTextWatcher)
    }

    private fun removeTouchListeners() {
        binding.editReference.setOnTouchListener(null)
        binding.editTrainName.setOnTouchListener(null)
        binding.editTrainDescription.setOnTouchListener(null)
        binding.editLocationNumber.setOnTouchListener(null)
        binding.editLocationLetter.setOnTouchListener(null)
        binding.editScale.setOnTouchListener(null)
        binding.editQuantity.setOnTouchListener(null)
    }
}
