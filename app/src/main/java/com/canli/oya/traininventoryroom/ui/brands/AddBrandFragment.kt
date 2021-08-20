package com.canli.oya.traininventoryroom.ui.brands

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentAddBrandBinding
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.ui.base.setMenuIcon
import com.canli.oya.traininventoryroom.utils.IS_EDIT
import com.canli.oya.traininventoryroom.utils.bindImage
import com.canli.oya.traininventoryroom.utils.shortToast
import com.canlioya.core.models.Brand
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class AddBrandFragment : Fragment(R.layout.fragment_add_brand) {

    private val binding by viewBinding(FragmentAddBrandBinding::bind)

    private val viewModel : BrandViewModel by viewModels({requireParentFragment()})

    private var brandId: Int = 0
    private var logoUri: Uri? = null
    private var isEditCase: Boolean = false

    private var brandList : List<String> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set click listeners
        binding.addBrandSaveBtn.setOnClickListener{ saveBrand() }
        binding.addBrandImage.setOnClickListener{ launchImagePicker()}

        //Request focus on the first edit text
        binding.addBrandEditBrandName.requestFocus()

        if (arguments?.getBoolean(IS_EDIT) == true) { //This is the "edit" case
            isEditCase = true
            viewModel.chosenItem.observe(viewLifecycleOwner,  { brandEntry ->
                brandEntry?.let {
                    binding.chosenBrand = it
                    brandId = it.brandId
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
                .compress(512)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(200, 200)    //Final image resolution will be less than 1080 x 1080(Optional)
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
        logoUri?.let { imagePath = logoUri.toString() }

        if (isEditCase) {
            //Construct a new BrandEntry object from this data with ID included
            val brandToUpdate = Brand(brandId, brandName, imagePath, webAddress)
            viewModel.updateItem(brandToUpdate)
        } else {
            //Construct a new BrandEntry object from this data (without ID)
            val newBrand = Brand(brandName = brandName, brandLogoUri = imagePath, webUrl = webAddress)
            //Insert to database in a background thread
            viewModel.insertItem(newBrand)
        }

        context?.shortToast(R.string.brand_Saved)

        clearFocusAndHideSoftKeyboard()

        if(isEditCase || parentFragment is AddTrainFragment){
            removeFragment()
        }
    }

    private fun removeFragment() {
        val currentInstance = if(parentFragment is AddTrainFragment) parentFragmentManager.findFragmentById(R.id.childFragContainer)
        else parentFragmentManager.findFragmentById(R.id.list_addFrag_container)

        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            currentInstance?.let {
                remove(it)
            }
        }

        if(parentFragment is BrandListFragment) {
            (parentFragment as BrandListFragment).addMenuItem?.setMenuIcon(R.drawable.avd_plus_to_cross)
        }
    }

    private fun clearFocusAndHideSoftKeyboard() {
        //Clear focus and hide soft keyboard
        binding.addBrandEditBrandName.text = null
        binding.addBrandEditWeb.text = null
        val focusedView = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusedView?.clearFocus()
        imm.hideSoftInputFromWindow(focusedView?.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // File object will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            Timber.d("Path:${uri}")

            binding.addBrandImage.bindImage(uri.toString(), ResourcesCompat.getDrawable(resources, R.drawable.ic_gallery_light, null))
        }
    }
}
