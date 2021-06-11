package com.canli.oya.traininventoryroom.ui.brands

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canli.oya.traininventoryroom.ui.base.BaseListAdapter
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseFrag
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canli.oya.traininventoryroom.utils.TRAINS_OF_BRAND
import com.canli.oya.traininventoryroom.utils.shortToast
import com.canlioya.core.models.Brand
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class BrandListFragment : BrandCategoryBaseFrag<Brand>(), BrandItemClickListener, SwipeDeleteListener<Brand> {

    private val brandViewModel : BrandViewModel by viewModels()

    override fun getListAdapter(): BaseListAdapter<Brand, BrandItemClickListener> = BrandAdapter(requireContext(), this, this)

    override fun getListViewModel(): BCBaseViewModel<Brand> = brandViewModel

    override fun onBrandItemClicked(view: View, clickedBrand: Brand) {
        when (view.id) {
            R.id.brand_item_web_icon -> openWebSite(clickedBrand)
            R.id.brand_item_train_icon -> launchTrainListWithBrand(clickedBrand.brandName)
            R.id.brand_item_edit_icon -> editItem(clickedBrand)
        }
    }

    private fun launchTrainListWithBrand(brandName: String){
        val action = BrandListFragmentDirections.actionBrandListFragmentToFilterTrainFragment(
            TRAINS_OF_BRAND, brandName = brandName)
        binding.root.findNavController().navigate(action)
    }

    private fun openWebSite(clickedBrand: Brand) {
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

    override fun getEmptyMessage(): Int = R.string.no_brands_found

}

