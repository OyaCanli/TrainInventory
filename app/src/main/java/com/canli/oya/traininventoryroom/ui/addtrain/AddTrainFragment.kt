package com.canli.oya.traininventoryroom.ui.addtrain

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddTrainBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.ui.brands.AddBrandFragment
import com.canli.oya.traininventoryroom.ui.categories.AddCategoryFragment
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.CHOSEN_TRAIN
import com.canli.oya.traininventoryroom.utils.IS_EDIT
import com.canli.oya.traininventoryroom.utils.clearFocusAndHideKeyboard
import com.canli.oya.traininventoryroom.utils.shortToast
import com.github.dhaval2404.imagepicker.ImagePicker
import timber.log.Timber
import javax.inject.Inject


class AddTrainFragment : Fragment(R.layout.fragment_add_train), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private val binding by viewBinding(FragmentAddTrainBinding::bind)

    private lateinit var addViewModel: AddTrainViewModel

    @Inject
    lateinit var viewModelFactory: AddTrainFactory

    private var isEdit: Boolean = false

    var chosenTrain: TrainEntry? = null

    private var brandList: List<BrandEntry> = ArrayList()
    private var categoryList: List<String> = ArrayList()

    private lateinit var categoryAdapter: CategorySpinAdapter
    private lateinit var brandAdapter: BrandSpinAdapter

    private var saveMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chosenTrain = arguments?.getParcelable(CHOSEN_TRAIN)
        isEdit = chosenTrain != null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        //Set click listener on buttons
        binding.addTrainAddBrandBtn.setOnClickListener(this)
        binding.addTrainAddCategoryBtn.setOnClickListener(this)
        binding.productDetailsGalleryImage.setOnClickListener(this)

        binding.categorySpinner.onItemSelectedListener = this
        binding.brandSpinner.onItemSelectedListener = this

        (activity as? MainActivity)?.supportActionBar?.title = if (isEdit) getString(R.string.edit_train) else getString(R.string.add_train)

        initDagger()

        addViewModel = ViewModelProvider(this, viewModelFactory).get(AddTrainViewModel::class.java)
        binding.viewModel = addViewModel

        setSpinners()
        getAndObserveCategories()
        getAndObserveBrands()
    }

    private fun initDagger() {
        val appComponent = ComponentProvider.getInstance(requireActivity().application).daggerComponent
        DaggerAddTrainComponent.builder()
                .appComponent(appComponent)
                .bindChosenTrain(chosenTrain)
                .build()
                .inject(this)
    }

    private fun setSpinners() {
        categoryAdapter = CategorySpinAdapter(requireActivity())
        binding.categorySpinner.adapter = categoryAdapter
        brandAdapter = BrandSpinAdapter(requireContext(), null)
        binding.brandSpinner.adapter = brandAdapter
    }

    private fun getAndObserveCategories() {
        addViewModel.categoryList.observe(viewLifecycleOwner, Observer { categoryEntries ->
            if (!categoryEntries.isNullOrEmpty()) {
                categoryAdapter.setCategories(categoryEntries)
                categoryList = categoryAdapter.categoryList

                if (isEdit) { //In edit mode, show the existing categoryName as selected
                    val index = categoryList.indexOf(chosenTrain?.categoryName)
                    binding.categorySpinner.setSelection(index)
                }
            }
        })
    }

    private fun getAndObserveBrands() {
        addViewModel.brandList.observe(viewLifecycleOwner, Observer { brandEntries ->
            if (!brandEntries.isNullOrEmpty()) {
                brandAdapter.setBrands(brandEntries)
                brandList = brandEntries

                if (isEdit) { //In edit mode, show the existing brandName as selected
                    val index = brandEntries.indexOfFirst { it.brandName == chosenTrain?.brandName }.plus(1)
                    binding.brandSpinner.setSelection(index)
                }
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addTrain_addBrandBtn -> insertAddBrandFragment()
            R.id.addTrain_addCategoryBtn -> insertAddCategoryFragment()
            R.id.product_details_gallery_image -> {
                ImagePicker.with(this)
                        .crop(1f, 1f)                //Crop Square image(Optional)
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_with_save, menu)
        saveMenuItem = menu.findItem(R.id.action_save)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.d("onOptionsItemSelected")
        when (item.itemId) {
            R.id.action_save -> saveTrain()
            android.R.id.home -> onBackClicked()
        }
        return true
    }

    fun onBackClicked() {
        Timber.d("onBackClicked is called")
        activity?.clearFocusAndHideKeyboard()
        if (addViewModel.isChanged) {
            showUnsavedChangesDialog()
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                val anim = saveMenuItem?.icon as AnimatedVectorDrawable
                anim.start()
            }
            findNavController().popBackStack()
        }
    }

    private fun insertAddCategoryFragment() {
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.childFragContainer, AddCategoryFragment())
        }
    }

    private fun insertAddBrandFragment() {
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.childFragContainer, AddBrandFragment())
        }
    }

    private fun saveTrain() {

        // DATA VALIDATION
        if (thereAreMissingValues()) return
        if (quantityIsNotValid()) return
        if (!isEdit && trainNameAlreadyExists()) return

        // SAVE
        addViewModel.saveTrain()

        //After adding the train, go back to where user come from.
        parentFragmentManager.popBackStack()
    }

    private fun thereAreMissingValues(): Boolean {
        val proposedTrain = addViewModel.trainBeingModified.get()
        return when {
            proposedTrain?.categoryName.isNullOrBlank() -> {
                context?.shortToast(R.string.category_name_empty)
                true
            }
            proposedTrain?.brandName.isNullOrBlank() -> {
                context?.shortToast(R.string.brand_name_empty)
                true
            }
            proposedTrain?.trainName.isNullOrBlank() -> {
                context?.shortToast(R.string.train_name_empty)
                true
            }
            else -> false
        }
    }

    private fun trainNameAlreadyExists(): Boolean {
        if (addViewModel.trainList.contains(addViewModel.trainBeingModified.get()?.trainName)) {
            context?.shortToast(R.string.train_name_already_Exists)
            return true
        }
        return false
    }

    private fun quantityIsNotValid(): Boolean {
        val quantityToParse = binding.editQuantity.text.toString().trim()
        //Quantity can be null. But if it is not null it should be a non-negative integer
        val quantity: Int
        if (!TextUtils.isEmpty(quantityToParse)) {
            try {
                quantity = Integer.valueOf(quantityToParse)
                if (quantity < 0) {
                    context?.shortToast(R.string.quantity_should_be_positive)
                    return true
                } else {
                    addViewModel.trainBeingModified.get()?.quantity = quantity
                }
            } catch (nfe: NumberFormatException) {
                context?.shortToast(R.string.quantity_should_be_positive)
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // File object will not be null for RESULT_OK
            val file = ImagePicker.getFile(data)

            Timber.d("Path:${file?.absolutePath}")

            Glide.with(this)
                    .load(file)
                    .into(binding.productDetailsGalleryImage)
            addViewModel.trainBeingModified.get()?.imageUri = Uri.fromFile(file).toString()
        }
    }

    private fun showUnsavedChangesDialog() {
        //If user clicks back when there are unsaved changes in AddTrainFragment, warn user with a dialog.
        val builder = AlertDialog.Builder(requireContext(), R.style.alert_dialog_style)
        with(builder) {
            setMessage(R.string.unsaved_changes_warning)
            setPositiveButton(getString(R.string.discard_changes)) { dialog, _ ->
                //Changes will be discarded
                findNavController().popBackStack()
                dialog?.dismiss()
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(spinner: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //the listener is attached to both spinners.
        //when statement differentiate which spinners is selected
        when (spinner?.id) {
            R.id.brandSpinner -> {
                addViewModel.trainBeingModified.get()?.brandName = if (position == 0) null else brandList[position - 1].brandName
            }
            R.id.categorySpinner -> {
                addViewModel.trainBeingModified.get()?.categoryName = if (position == 0) null else categoryList[position]
            }
        }
    }
}


