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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.*
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.BrandCategoryList
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class CategoryListFragment : Fragment(), CategoryItemClickListener, SwipeDeleteListener<CategoryEntry>, CoroutineScope {

    private lateinit var binding: BrandCategoryList

    private val mViewModel by viewModels<CategoryViewModel>()

    private lateinit var categoryListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + categoryListJob

    private lateinit var mAdapter: CategoryAdapter
    private var mCategories: List<CategoryEntry> = emptyList()

    private var addMenuItem: MenuItem? = null

    private var addFragVisible = false

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list_with_framelayout, container, false)

        setHasOptionsMenu(true)

        categoryListJob = Job()

        mAdapter = CategoryAdapter(this, this)

        with(binding.includedList.list) {
            addItemDecoration(getItemDivider(context))
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.includedList.uiState = mViewModel.categoryListUiState

        mViewModel.categoryList.observe(this, Observer { categoryEntries ->
            if (categoryEntries.isNullOrEmpty()) {
                mViewModel.categoryListUiState.showEmpty = true
                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.includedList.emptyImage.startAnimation(animation)
                if (!addFragVisible) {
                    blinkAddMenuItem()
                }
            } else {
                mAdapter.submitList(categoryEntries)
                mCategories = categoryEntries
                mViewModel.categoryListUiState.showList = true
            }
        })

        activity?.title = getString(R.string.all_categories)

        mViewModel.isChildFragVisible.observe(this, Observer { isChildFragVisible ->
            addFragVisible = isChildFragVisible
        })

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), mAdapter)).attachToRecyclerView(binding.includedList.list)
    }

    override fun onDeleteConfirmed(itemToDelete: CategoryEntry, position : Int) {
        Timber.d("delete is confirmed")
        launch {
            //First check whether this category is used by trains table
            val isUsed = withContext(Dispatchers.IO) { mViewModel.isThisCategoryUsed(itemToDelete.categoryName) }
            if (isUsed) {
                // If it is used, show a warning and don't let user delete this
                context?.toast(R.string.cannot_erase_category)
                mAdapter.cancelDelete(position)
            } else {
                //If it is not used, erase the category
                mViewModel.deleteCategory(itemToDelete)
                mAdapter.itemDeleted(position)
            }
        }
    }

    override fun onDeleteCanceled(position: Int) = mAdapter.cancelDelete(position)

    private fun blinkAddMenuItem() {
        if (Build.VERSION.SDK_INT >= 23) {
            addMenuItem?.setIcon(R.drawable.avd_blinking_plus)
            val blinkingAnim = addMenuItem?.icon as AnimatedVectorDrawable
            blinkingAnim.clearAnimationCallbacks()
            blinkingAnim.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    if(!addFragVisible){
                        addMenuItem?.setIcon(R.drawable.avd_plus_to_cross)
                    }
                }
            })
            blinkingAnim.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
        addMenuItem = menu.getItem(0)
        if(addFragVisible){
            addMenuItem?.setIcon((R.drawable.avd_cross_to_plus))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            val icon : Int = if (mViewModel.isChildFragVisible.value == false) {
                openAddCategoryFragment()
                R.drawable.avd_cross_to_plus
            } else {
                removeAddCategoryFragment()
                R.drawable.avd_plus_to_cross
            }
            startAnimationOnMenuItem(item, icon)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startAnimationOnMenuItem(item: MenuItem, @DrawableRes iconRes : Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val avd: AnimatedVectorDrawable = item.icon as AnimatedVectorDrawable
            avd.clearAnimationCallbacks()
            avd.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    item.setIcon(iconRes)
                }
            })
            avd.start()
        }
    }

    private fun removeAddCategoryFragment() {
        val childFrag = childFragmentManager.findFragmentById(R.id.list_addFrag_container)
        childFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            childFrag?.let { remove(it) }
        }
        mViewModel.setIsChildFragVisible(false)
    }

    private fun openAddCategoryFragment() {
        val addCatFrag = AddCategoryFragment()
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, addCatFrag)
        }
        mViewModel.setIsChildFragVisible(true)
    }

    override fun onCategoryItemClicked(view: View, category: CategoryEntry) {
        Timber.d("Category item clicked")
        when(view.id){
            R.id.category_item_train_icon -> openTrainsForThisCategory(category.categoryName)
            R.id.category_item_edit_icon -> editCategory(category)
        }
    }

    private fun editCategory(chosenCategory : CategoryEntry) {
        Timber.d("Edit category is called")
        mViewModel.setChosenCategory(chosenCategory)
        val addCategoryFrag = AddCategoryFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, EDIT_CASE)
        addCategoryFrag.arguments = args
        mViewModel.setIsChildFragVisible(true)
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, addCategoryFrag)
        }
    }

    private fun openTrainsForThisCategory(categoryName : String) {
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
        args.putString(CATEGORY_NAME, categoryName)
        trainListFrag.arguments = args
        fragmentManager?.commit {
            replace(R.id.container, trainListFrag)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryListJob.cancel()
    }
}
