package com.canli.oya.traininventoryroom.ui.brands

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.BaseAdapter
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseFrag
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseVM
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canli.oya.traininventoryroom.utils.TRAINS_OF_BRAND
import com.canli.oya.traininventoryroom.utils.shortToast
import timber.log.Timber
import javax.inject.Inject

class BrandListFragment : BrandCategoryBaseFrag<BrandEntry>(), BrandItemClickListener, SwipeDeleteListener<BrandEntry> {

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    override fun getListAdapter(): BaseAdapter<BrandEntry, BrandItemClickListener> = BrandAdapter(requireContext(), this, this)

    override fun getListViewModel(): BrandCategoryBaseVM<BrandEntry> = ViewModelProvider(this, viewModelFactory).get(BrandViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)
    }

    override fun onBrandItemClicked(view: View, clickedBrand: BrandEntry) {
        when (view.id) {
            R.id.brand_item_web_icon -> openWebSite(clickedBrand)
            R.id.brand_item_train_icon -> launchTrainListWithBrand(clickedBrand.brandName)
            R.id.brand_item_edit_icon -> editItem(clickedBrand)
        }
    }

    private fun launchTrainListWithBrand(brandName: String){
        val action = BrandListFragmentDirections.actionBrandListFragmentToTrainListFragment(
            TRAINS_OF_BRAND, brandName = brandName)
        binding.root.findNavController().navigate(action)
    }

    private fun openWebSite(clickedBrand: BrandEntry) {
        val urlString = clickedBrand.webUrl
        var webUri: Uri? = null
        if (!TextUtils.isEmpty(urlString)) {
            try {
                webUri = Uri.parse(urlString)
            } catch (e: Exception) {
                Timber.e(e.toString())
            }

            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = webUri
            if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(webIntent)
            }
        } else {
            context?.shortToast(getString(R.string.no_website_warning))
        }
    }

    override fun getChildFragment(): Fragment = AddBrandFragment()

    override fun getTitle(): String  = getString(R.string.all_brands)
}

