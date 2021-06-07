package com.canli.oya.traininventoryroom.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.BCBaseViewModel
import com.canli.oya.traininventoryroom.ui.base.BaseListAdapter
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseFrag
import com.canli.oya.traininventoryroom.utils.TRAINS_OF_CATEGORY
import com.canlioya.core.models.Category
import timber.log.Timber
import javax.inject.Inject


class CategoryListFragment : BrandCategoryBaseFrag<Category>(), CategoryItemClickListener {

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    override fun getListAdapter(): BaseListAdapter<Category, out Any> = CategoryAdapter(requireContext(), this, this)

    override fun getListViewModel(): BCBaseViewModel<Category> = ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)
    }

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
