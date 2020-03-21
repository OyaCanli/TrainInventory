package com.canli.oya.traininventoryroom.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.BaseAdapter
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseFrag
import com.canli.oya.traininventoryroom.ui.base.BrandCategoryBaseVM
import com.canli.oya.traininventoryroom.ui.main.Navigator
import timber.log.Timber
import javax.inject.Inject


class CategoryListFragment : BrandCategoryBaseFrag<CategoryEntry>(), CategoryItemClickListener {

    @Inject
    lateinit var navigator : Navigator

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    override fun getListAdapter(): BaseAdapter<CategoryEntry, out Any> = CategoryAdapter(requireContext(), this, this)

    override fun getListViewModel(): BrandCategoryBaseVM<CategoryEntry> = ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)
    }

    override fun onCategoryItemClicked(view: View, category: CategoryEntry) {
        Timber.d("Category item clicked")
        when(view.id){
            R.id.category_item_train_icon -> navigator.launchTrainList_withThisCategory(category.categoryName)
            R.id.category_item_edit_icon -> editItem(category)
        }
    }

    override fun getChildFragment(): Fragment = AddCategoryFragment()

    override fun getTitle(): String = getString(R.string.all_categories)
}
