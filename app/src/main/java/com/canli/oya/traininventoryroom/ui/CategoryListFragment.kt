package com.canli.oya.traininventoryroom.ui

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.CategoryAdapter
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.BrandCategoryList
import com.canli.oya.traininventoryroom.utils.CATEGORY_NAME
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.TRAINS_OF_CATEGORY
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import kotlinx.coroutines.*
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.toast
import kotlin.coroutines.CoroutineContext

class CategoryListFragment : Fragment(), CategoryAdapter.CategoryItemClickListener, CoroutineScope {

    private lateinit var binding: BrandCategoryList

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private lateinit var categoryListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + categoryListJob

    private lateinit var mAdapter: CategoryAdapter
    private var mCategories: List<String> = emptyList()

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

        with(binding.included.list) {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(divider)
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.included.uiState = mViewModel.categoryListUiState

        mViewModel.categoryList?.observe(this@CategoryListFragment, Observer { categoryEntries ->
            if (categoryEntries.isNullOrEmpty()) {
                mViewModel.categoryListUiState.showEmpty = true
                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.included.emptyImage.startAnimation(animation)
            } else {
                mAdapter.categoryList = categoryEntries
                mCategories = categoryEntries
                mViewModel.categoryListUiState.showList = true
            }
        })

        activity?.title = getString(R.string.all_categories)

        //This part is for providing swipe-to-delete functionality, as well as a snack bar to undo deleting
        val coordinator = activity!!.findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.coordinator)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //First take a backup of the category to erase
                val categoryToErase = CategoryEntry(mCategories[position])

                //Remove the category from the database
                launch {
                    //First check whether this category is used by trains table
                    val isUsed = withContext(Dispatchers.IO){mViewModel.isThisCategoryUsed(categoryToErase.categoryName)}
                    if (isUsed) {
                        // If it is used, show a warning and don't let user delete this
                        context?.toast(R.string.cannot_erase_category)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        //If it is not used, erase the category
                        mViewModel.deleteCategory(categoryToErase)
                        //Show a snack bar for undoing delete
                        coordinator?.indefiniteSnackbar(R.string.category_deleted, R.string.undo){
                            mViewModel.insertCategory(categoryToErase)
                        }
                    }
                }
            }
        }).attachToRecyclerView(binding.included.list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            openAddCategoryFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAddCategoryFragment() {
        val addCatFrag = AddCategoryFragment()
        childFragmentManager.transaction {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.brandlist_addFrag_container, addCatFrag)
        }
    }

    override fun onCategoryItemClicked(categoryName: String) {
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
        args.putString(CATEGORY_NAME, categoryName)
        trainListFrag.arguments = args
        fragmentManager?.transaction {
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
