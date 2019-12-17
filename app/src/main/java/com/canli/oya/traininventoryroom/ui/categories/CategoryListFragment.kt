package com.canli.oya.traininventoryroom.ui.categories

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.EDIT_CASE
import com.canli.oya.traininventoryroom.common.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.common.SwipeDeleteListener
import com.canli.oya.traininventoryroom.common.SwipeToDeleteCallback
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.Navigator
import com.canli.oya.traininventoryroom.ui.base.BaseListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject


class CategoryListFragment : BaseListFragment(), CategoryItemClickListener, SwipeDeleteListener<CategoryEntry> {

    private lateinit var binding: FragmentListBinding

    @Inject
    lateinit var navigator : Navigator

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private lateinit var viewModel : CategoryViewModel

    private lateinit var mAdapter: CategoryAdapter
    private var mCategories: List<CategoryEntry> = emptyList()

    var addMenuItem: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false)

        mAdapter = CategoryAdapter(requireContext(), this, this)
        binding.list.adapter = mAdapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("onActivityCreated is called")

        (activity?.application as TrainApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

        binding.uiState = viewModel.listUiState

        viewModel.allItems.observe(viewLifecycleOwner, Observer { categoryEntries ->
            if (categoryEntries.isNullOrEmpty()) {
                viewModel.listUiState.showEmpty = true
                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.emptyImage.startAnimation(animation)
                //If there are no items and add is not clicked, blink add button to draw user's attention
                if (!viewModel.isChildFragVisible) {
                    addMenuItem?.let { blinkAddMenuItem(it, R.drawable.avd_plus_to_cross) }
                }
            } else {
                mAdapter.submitList(categoryEntries)
                mCategories = categoryEntries
                viewModel.listUiState.showList = true
            }
        })

        activity?.title = getString(R.string.all_categories)

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), mAdapter)).attachToRecyclerView(binding.list)
    }

    override fun onDeleteConfirmed(itemToDelete: CategoryEntry, position : Int) {
        Timber.d("delete is confirmed")
        launch {
            //First check whether this category is used by trains table
            val isUsed = withContext(Dispatchers.IO) { viewModel.isThisItemUsed(itemToDelete.categoryName) }
            if (isUsed) {
                // If it is used, show a warning and don't let user delete this
                context?.toast(R.string.cannot_erase_category)
                mAdapter.cancelDelete(position)
            } else {
                //If it is not used, erase the category
                viewModel.deleteItem(itemToDelete)
                mAdapter.itemDeleted(position)
            }
        }
    }

    override fun onDeleteCanceled(position: Int) = mAdapter.cancelDelete(position)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.d("onCreateOptionsMEnu is created")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
        addMenuItem = menu.getItem(0)
        if(viewModel.isChildFragVisible){
            Timber.d("fragment seems to be visible in viewmodel")
            addMenuItem?.setIcon((R.drawable.avd_cross_to_plus))
        } else {
            addMenuItem?.setIcon((R.drawable.avd_plus_to_cross))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            val icon : Int = if (viewModel.isChildFragVisible) {
                removeAddCategoryFragment()
                viewModel.isChildFragVisible = false
                R.drawable.avd_plus_to_cross
            } else {
                openAddEditCategoryFragment(AddCategoryFragment())
                viewModel.isChildFragVisible = true
                R.drawable.avd_cross_to_plus
            }
            startAnimationOnMenuItem(item, icon)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startAnimationOnMenuItem(item: MenuItem, @DrawableRes iconRes : Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val avd = item.icon as? AnimatedVectorDrawable
            avd?.clearAnimationCallbacks()
            avd?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    item.setIcon(iconRes)
                }
            })
            avd?.start()
        }
    }

    private fun removeAddCategoryFragment() {
        val childFrag = childFragmentManager.findFragmentById(R.id.list_addFrag_container)
        childFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            childFrag?.let { remove(it) }
        }
    }

    private fun openAddEditCategoryFragment(frag: AddCategoryFragment) {
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, frag)
        }
    }

    override fun onCategoryItemClicked(view: View, category: CategoryEntry) {
        Timber.d("Category item clicked")
        when(view.id){
            R.id.category_item_train_icon -> navigator.launchTrainList_withThisCategory(category.categoryName)
            R.id.category_item_edit_icon -> editCategory(category)
        }
    }

    private fun editCategory(chosenCategory : CategoryEntry) {
        Timber.d("Edit category is called")
        viewModel.setChosenItem(chosenCategory)
        val addCategoryFrag = AddCategoryFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, EDIT_CASE)
        addCategoryFrag.arguments = args
        openAddEditCategoryFragment(addCategoryFrag)
        viewModel.isChildFragVisible = true
        addMenuItem?.let {
            startAnimationOnMenuItem(it, R.drawable.avd_cross_to_plus)
        }
    }
}
