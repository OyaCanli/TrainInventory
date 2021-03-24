package com.canli.oya.traininventoryroom.ui.brands

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddBrandBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.shortToast
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class AddBrandFragment : Fragment(R.layout.fragment_add_brand) {

    private val binding by viewBinding(FragmentAddBrandBinding::bind)

    private lateinit var viewModel : BrandViewModel

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private var mBrandId: Int = 0
    private var mLogoUri: Uri? = null
    private var isEditCase: Boolean = false

    private var brandList : List<String> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set click listeners
        binding.addBrandSaveBtn.setOnClickListener{ saveBrand() }
        binding.addBrandImage.setOnClickListener{ launchImagePicker()}

        //Request focus on the first edit text
        binding.addBrandEditBrandName.requestFocus()

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        viewModel = ViewModelProvider(requireParentFragment(), viewModelFactory).get(BrandViewModel::class.java)

        if (arguments?.containsKey(INTENT_REQUEST_CODE) == true) { //This is the "edit" case
            isEditCase = true
            viewModel.chosenItem.observe(viewLifecycleOwner,  { brandEntry ->
                brandEntry?.let {
                    binding.chosenBrand = it
                    mBrandId = it.brandId
                }
            })
        }

        lifecycleScope.launch {
            viewModel.allItems.collectLatest { brands ->
                brandList = brands.map { it.brandName }
            }
        }
    }

    private fun launchImagePicker() {
        ImagePicker.with(this)
                .crop(1f, 1f)                //Crop Square image(Optional)
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
    }

    private fun saveBrand() {
        //Get brand name from edit text and verify it is not empty
        val brandName = binding.addBrandEditBrandName.text?.toString()?.trim()
        if(brandName.isNullOrBlank()){
            context?.shortToast(getString(com.canli.oya.traininventoryroom.R.string.brand_cannot_be_empty))
            return
        }

        if(!isEditCase && brandList.contains(brandName)){
            context?.shortToast(getString(R.string.brand_already_exists))
            return
        }

        //Get web address from edit text
        val webAddress = binding.addBrandEditWeb.text?.toString()?.trim()

        //If there is a uri for logo image, parse it to string
        var imagePath: String? = null
        mLogoUri?.let { imagePath = mLogoUri.toString() }

        if (isEditCase) {
            //Construct a new BrandEntry object from this data with ID included
            val brandToUpdate = BrandEntry(mBrandId, brandName, imagePath, webAddress)
            viewModel.updateItem(brandToUpdate)
        } else {
            //Construct a new BrandEntry object from this data (without ID)
            val newBrand = BrandEntry(brandName = brandName, brandLogoUri = imagePath, webUrl = webAddress)
            //Insert to database in a background thread
            viewModel.insertItem(newBrand)
        }

        context?.shortToast(R.string.brand_Saved)

        clearFocusAndHideSoftKeyboard()

        if(parentFragment is AddTrainFragment){
            removeFragment()
        }
    }

    private fun removeFragment() {
        val currentInstance = parentFragmentManager.findFragmentById(R.id.childFragContainer)
        parentFragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_CLOSE)
            remove(currentInstance!!)
        }
    }

    private fun clearFocusAndHideSoftKeyboard() {
        //Clear focus and hide soft keyboard
        binding.addBrandEditBrandName.text = null
        val focusedView = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusedView?.clearFocus()
        imm.hideSoftInputFromWindow(focusedView?.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // File object will not be null for RESULT_OK
            val file = ImagePicker.getFile(data)

            Timber.e("Path:${file?.absolutePath}")

            Glide.with(this)
                    .load(file)
                    .into(binding.addBrandImage)
            mLogoUri = Uri.fromFile(file)
        }
    }
}
