package com.canli.oya.traininventoryroom.ui.brands

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddBrandBinding
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject


class AddBrandFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentAddBrandBinding

    private lateinit var viewModel : BrandViewModel

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private var mBrandId: Int = 0
    private var mLogoUri: Uri? = null
    private var isEditCase: Boolean = false

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

        (activity?.application as TrainApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(parentFragment!!, viewModelFactory).get(BrandViewModel::class.java)

        if (arguments?.containsKey(INTENT_REQUEST_CODE) == true) { //This is the "edit" case
            isEditCase = true
            viewModel.chosenBrand.observe(viewLifecycleOwner, Observer { brandEntry ->
                brandEntry?.let {
                    binding.chosenBrand = it
                    mBrandId = it.brandId
                }
            })
        }
    }

    override fun onClick(v: View) {
        //If save is clicked
        if (v.id == R.id.addBrand_saveBtn) {
            saveBrand()
        } else {
            //If add photo is clicked
            ImagePicker.with(this)
                    .crop(1f, 1f)	    		//Crop Square image(Optional)
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
        }
    }

    private fun saveBrand() {
        //Get brand name from edit text and verify it is not empty
        val brandName = binding.addBrandEditBrandName.text?.toString()?.trim()
        if(brandName.isNullOrBlank()){
            context?.toast(getString(com.canli.oya.traininventoryroom.R.string.brand_cannot_be_empty))
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
            viewModel.updateBrand(brandToUpdate)
        } else {
            //Construct a new BrandEntry object from this data (without ID)
            val newBrand = BrandEntry(brandName = brandName, brandLogoUri = imagePath, webUrl = webAddress)
            //Insert to database in a background thread
            viewModel.insertBrand(newBrand)
        }

        context?.toast(R.string.brand_Saved)

        clearFocusAndHideSoftKeyboard()
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
