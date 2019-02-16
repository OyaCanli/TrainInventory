package com.canli.oya.traininventoryroom.ui

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.CategoryAdapter
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentBrandlistBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class CategoryListFragment : androidx.fragment.app.Fragment(), CategoryAdapter.CategoryItemClickListener {

    private lateinit var binding: FragmentBrandlistBinding

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private lateinit var mAdapter: CategoryAdapter
    private var mCategories: List<String> = emptyList()

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_brandlist, container, false)

        setHasOptionsMenu(true)

        mAdapter = CategoryAdapter(this)

        with(binding.included.list){
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel.loadCategoryList(InjectorUtils.provideCategoryRepo(requireContext()))
        mViewModel.categoryList?.observe(this@CategoryListFragment, Observer { categoryEntries ->
            if (categoryEntries.isNullOrEmpty()) {
                binding.included.isEmpty = true
                binding.included.emptyMessage = getString(R.string.no_categories_found)
                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.included.emptyImage.startAnimation(animation)
            } else {
                mAdapter.categoryList =categoryEntries
                mCategories = categoryEntries
                binding.included.isEmpty = false
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
                AppExecutors.instance.diskIO().execute {
                    //First check whether this category is used by trains table
                    if (mViewModel.isThisCategoryUsed(categoryToErase.categoryName)) {
                        // If it is used, show a warning and don't let user delete this
                        activity?.runOnUiThread {
                            context?.toast(R.string.cannot_erase_category)
                            mAdapter.notifyDataSetChanged()
                        }
                    } else {
                        //If it is not used, erase the category
                        mViewModel.deleteCategory(categoryToErase)
                        //Show a snack bar for undoing delete
                        val snackbar = Snackbar
                                .make(coordinator, R.string.category_deleted, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.undo) {
                                    //If UNDO is clicked, add the item back in the database
                                    mViewModel.insertCategory(categoryToErase)
                                }
                        snackbar.show()
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
        childFragmentManager.transaction { setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addCatFrag) }
    }

    override fun onCategoryItemClicked(categoryName: String) {
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
        args.putString(CATEGORY_NAME, categoryName)
        trainListFrag.arguments = args
        fragmentManager?.transaction { replace(R.id.container, trainListFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)}
    }

}
