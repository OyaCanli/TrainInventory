package com.canli.oya.traininventoryroom.ui.addtrain

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddTrainBinding
import com.canli.oya.traininventoryroom.ui.brands.AddBrandFragment
import com.canli.oya.traininventoryroom.ui.categories.AddCategoryFragment
import com.canli.oya.traininventoryroom.utils.CHOSEN_TRAIN
import com.canli.oya.traininventoryroom.utils.provideAddTrainFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import timber.log.Timber


class AddTrainFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentAddTrainBinding

    private lateinit var addViewModel: AddTrainViewModel

    private var isEdit: Boolean = false

    var chosenTrain: TrainEntry? = null

    private var brandList: List<BrandEntry> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()

    private val disposable = CompositeDisposable()

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var brandAdapter: CustomSpinAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_train, container, false)

        setHasOptionsMenu(true)

        //Set click listener on buttons
        binding.addTrainAddBrandBtn.setOnClickListener(this)
        binding.addTrainAddCategoryBtn.setOnClickListener(this)
        binding.productDetailsGalleryImage.setOnClickListener(this)

        binding.categorySpinner.onItemSelectedListener = this
        binding.brandSpinner.onItemSelectedListener = this

        activity?.title = if (isEdit) getString(R.string.edit_train)
        else getString(R.string.add_train)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        chosenTrain = arguments?.getParcelable(CHOSEN_TRAIN)

        addViewModel = ViewModelProvider(this, provideAddTrainFactory(requireActivity(), chosenTrain)).get(AddTrainViewModel::class.java)
        binding.viewModel = addViewModel

        setBrandSpinner()
        setCategorySpinner()
        getAndObserveCategories()
        getAndObserveBrands()
    }

    private fun setCategorySpinner() {
        categoryList.add(getString(R.string.select_category))
        categoryAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, categoryList)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter
    }

    private fun getAndObserveCategories() {
        disposable.add(addViewModel.categoryList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        //onNext
                        { categoryEntries ->
                            if (!categoryEntries.isNullOrEmpty()) {
                                categoryList.clear()
                                categoryList.add(getString(R.string.select_category))
                                categoryList.addAll(categoryEntries.map { categoryEntry -> categoryEntry.categoryName})
                                categoryAdapter.notifyDataSetChanged()
                                val index = categoryList.indexOf(chosenTrain?.categoryName)
                                binding.categorySpinner.setSelection(index)
                            }
                        },
                        //onError
                        { error ->
                            Timber.e("Unable to get category list ${error.message}")
                        })
        )
    }

    private fun setBrandSpinner() {
        brandAdapter = CustomSpinAdapter(requireContext(), null)
        binding.brandSpinner.adapter = brandAdapter
    }

    private fun getAndObserveBrands() {
        disposable.add(addViewModel.brandList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        { brandEntries ->
                            if (!brandEntries.isNullOrEmpty()) {
                                brandAdapter.mBrandList = brandEntries
                                brandAdapter.notifyDataSetChanged()
                                brandList = brandEntries
                                val index = brandEntries.indexOfFirst { it.brandName == chosenTrain?.brandName }.plus(1)
                                binding.brandSpinner.setSelection(index)
                            }
                        },
                        // onError
                        { error ->
                            Timber.e("Unable to get brand list ${error.message}")
                        })
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addTrain_addBrandBtn -> insertAddBrandFragment()
            R.id.addTrain_addCategoryBtn -> insertAddCategoryFragment()
            R.id.product_details_gallery_image -> {
                ImagePicker.with(this)
                        .crop(1f, 1f)	    		//Crop Square image(Optional)
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
            }
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
            context?.toast(getString(R.string.scale_cant_be_empty))
            return true
        }
        return false
    }

    private fun brandNameIsEmpty(): Boolean {
        if (addViewModel.trainBeingModified.get()?.brandName == null) {
            context?.toast(getString(R.string.brand_name_empty))
            return true
        }
        return false
    }

    private fun categoryNameIsEmpty(): Boolean {
        if (addViewModel.trainBeingModified.get()?.categoryName == null) {
            context?.toast(getString(R.string.category_name_empty))
            return true
        }
        return false
    }

    private fun trainNameIsEmpty(): Boolean {
        if (addViewModel.trainBeingModified.get()?.trainName == null) {
            context?.toast(getString(R.string.train_name_empty))
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

    fun onBackClicked() {
        if (addViewModel.isChanged) {
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

    override fun onStop() {
        super.onStop()

        // clear all the subscription
        disposable.clear()
    }
}
