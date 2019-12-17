package com.canli.oya.traininventoryroom.ui.trains

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.*
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.Navigator
import com.canli.oya.traininventoryroom.ui.base.BaseListFragment
import javax.inject.Inject

class TrainListFragment : BaseListFragment(), TrainItemClickListener, SwipeDeleteListener<TrainMinimal> {

    private lateinit var viewModel: TrainViewModel

    @Inject
    lateinit var navigator : Navigator

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

    private lateinit var binding: FragmentListBinding
    private lateinit var mAdapter: TrainAdapter
    private var mTrainList: PagedList<TrainMinimal>? = null

    private var addMenuItem: MenuItem? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false)
        setHasOptionsMenu(true)

        //Set recycler view
        mAdapter = TrainAdapter(requireContext(), this, this)
        binding.list.adapter = mAdapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity?.application as TrainApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        binding.uiState = viewModel.trainListUiState

        when (arguments?.getString(INTENT_REQUEST_CODE)) {
            //If the fragment will be used for showing trains from a specific brand
            TRAINS_OF_BRAND -> {
                val brandName = arguments?.getString(BRAND_NAME) ?: return
                activity?.title = getString(R.string.trains_of_the_brand, brandName)
                viewModel.getTrainsFromThisBrand(brandName).observe(viewLifecycleOwner, Observer { trainEntries ->
                    evaluateResults(trainEntries, R.string.no_train_for_this_brand)
                })
            }
            //If the fragment_list will be used for showing trains from a specific category
            TRAINS_OF_CATEGORY -> {
                val categoryName = arguments?.getString(CATEGORY_NAME) ?: return
                activity?.title = getString(R.string.all_from_this_Category, categoryName)
                viewModel.getTrainsFromThisCategory(categoryName).observe(viewLifecycleOwner, Observer { trainEntries ->
                    evaluateResults(trainEntries, R.string.no_train_for_this_category)
                })
            }
            else -> {
                //If the fragment_list is going to be use for showing all trains, which is the default behaviour
                activity?.title = getString(R.string.all_trains)
                viewModel.trainList.observe(viewLifecycleOwner, Observer { trainEntries ->
                    evaluateResults(trainEntries, R.string.no_trains_found, true)
                })
            }
        }


        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), mAdapter)).attachToRecyclerView(binding.list)
    }

    private fun evaluateResults(trainEntries: PagedList<TrainMinimal>?, @StringRes message: Int, noTrain: Boolean = false) {
        if (trainEntries.isNullOrEmpty()) {
            viewModel.trainListUiState.emptyMessage = message
            viewModel.trainListUiState.showEmpty = true
            animateTrainLogo()
            if (noTrain) {
                addMenuItem?.let { blinkAddMenuItem(it, R.drawable.avd_plus_to_save) }
            }
        } else {
            mAdapter.submitList(trainEntries)
            mTrainList = trainEntries
            viewModel.trainListUiState.showList = true
        }
    }



    override fun onListItemClick(trainId: Int) = navigator.launchTrainDetails(trainId)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_and_add, menu)
        addMenuItem = menu.findItem(R.id.action_add)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        //added filter to fragment_list (dynamic change input text)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                filterTrains(query)
                return false
            }
        })
    }

    private fun filterTrains(query: String?) {
        if (query.isNullOrBlank()) {
            mAdapter.submitList(mTrainList)
        } else {
            viewModel.searchInTrains(query).observe(this, Observer { filteredTrains ->
                if (filteredTrains.isNotEmpty()) {
                    mAdapter.submitList(filteredTrains)
                    viewModel.searchInTrains(query).removeObservers(this)
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            if (Build.VERSION.SDK_INT >= 23) {
                addMenuItem?.setIcon(R.drawable.avd_plus_to_save)
                val anim = addMenuItem?.icon as? AnimatedVectorDrawable
                anim?.start()
            }
            navigator.launchAddTrain()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun animateTrainLogo() {
        val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
        binding.emptyImage.startAnimation(animation)
    }

    override fun onDeleteConfirmed(itemToDelete: TrainMinimal, position: Int) {
        viewModel.deleteTrain(itemToDelete.trainId)
        mAdapter.itemDeleted(position)
    }

    override fun onDeleteCanceled(position: Int) = mAdapter.cancelDelete(position)

    fun scrollToTop() {
        binding.list.smoothScrollToPosition(0)
    }
}
