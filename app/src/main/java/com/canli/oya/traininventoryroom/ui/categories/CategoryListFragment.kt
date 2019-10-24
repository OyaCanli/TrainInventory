package com.canli.oya.traininventoryroom.ui.categories

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.*
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.BrandCategoryList
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.toast
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class CategoryListFragment : Fragment(), CategoryAdapter.CategoryItemClickListener, CoroutineScope {

    private lateinit var binding: BrandCategoryList

    private val mViewModel by viewModels<CategoryViewModel>()

    private lateinit var categoryListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + categoryListJob

    private lateinit var mAdapter: CategoryAdapter
    private var mCategories: List<CategoryEntry> = emptyList()

    private val disposable = CompositeDisposable()

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list_with_framelayout, container, false)

        setHasOptionsMenu(true)

        categoryListJob = Job()

        mAdapter = CategoryAdapter(this)
        val divider = DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL)
        divider.setDrawable(ShapeDrawable().apply {
            intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.divider_height)
            paint.color = resources.getColor(R.color.divider_color)
        })

        with(binding.includedList.list) {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(divider)
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.includedList.uiState = mViewModel.categoryListUiState

        disposable.add(mViewModel.categoryList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        { categoryEntries ->
                            if (categoryEntries.isNullOrEmpty()) {
                                mViewModel.categoryListUiState.showEmpty = true
                                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                                binding.includedList.emptyImage.startAnimation(animation)
                            } else {
                                mAdapter.submitList(categoryEntries)
                                mCategories = categoryEntries
                                mViewModel.categoryListUiState.showList = true
                            }
                        },
                        // onError
                        { error ->
                            Timber.e("Unable to get category list ${error.message}")
                        }
                )
        )

        activity?.title = getString(R.string.all_categories)

        //This part is for providing swipe-to-delete functionality, as well as a snack bar to undo deleting
        val rootView = activity!!.findViewById<FrameLayout>(R.id.container)

        ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //First take a backup of the category to erase
                val categoryToErase = mCategories[position]

                //Remove the category from the database
                launch {
                    //First check whether this category is used by trains table
                    val isUsed = withContext(Dispatchers.IO) { mViewModel.isThisCategoryUsed(categoryToErase.categoryName) }
                    if (isUsed) {
                        // If it is used, show a warning and don't let user delete this
                        context.toast(R.string.cannot_erase_category)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        //If it is not used, erase the category
                        mViewModel.deleteCategory(categoryToErase)
                        //Show a snack bar for undoing delete
                        rootView?.longSnackbar(R.string.category_deleted, R.string.undo) {
                            mViewModel.insertCategory(categoryToErase)
                        }
                    }
                }
            }
        }).attachToRecyclerView(binding.includedList.list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            val icon : Int = if (mViewModel.isChildFragVisible.value == false) {
                openAddCategoryFragment()
                R.drawable.cross_to_plus_avd
            } else {
                removeAddCategoryFragment()
                R.drawable.plus_to_cross_avd
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

    override fun onStop() {
        super.onStop()
        // clear all the subscription
        disposable.clear()
    }

}
