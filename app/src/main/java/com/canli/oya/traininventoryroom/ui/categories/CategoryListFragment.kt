package com.canli.oya.traininventoryroom.ui.categories

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canli.oya.traininventoryroom.ui.base.BaseListAdapter
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseFrag
import com.canli.oya.traininventoryroom.utils.TRAINS_OF_CATEGORY
import com.canlioya.core.models.Category
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CategoryListFragment : BrandCategoryBaseFrag<Category>(), CategoryItemClickListener {

    private val categoryViewModel : CategoryViewModel by viewModels()

    override fun getListAdapter(): BaseListAdapter<Category, out Any> = CategoryAdapter(requireContext(), this, this)

    override fun getListViewModel(): BCBaseViewModel<Category> = categoryViewModel

    override fun onCategoryItemClicked(view: View, category: Category) {
        Timber.d("Category item clicked")
        when(view.id){
            R.id.category_item_train_icon -> launchTrainListWithCategory(category.categoryName)
            R.id.category_item_edit_icon -> editItem(category)
        }
    }

    private fun launchTrainListWithCategory(categoryName : String){
        val action = CategoryListFragmentDirections.actionCategoryListFragmentToFilterTrainFragment(TRAINS_OF_CATEGORY, categoryName = categoryName)
        binding.root.findNavController().navigate(action)
    }

    override fun getChildFragment(): Fragment = AddCategoryFragment()

    override fun getTitle(): String = getString(R.string.all_categories)

    override fun getEmptyMessage(): Int = R.string.no_categories_found
}
