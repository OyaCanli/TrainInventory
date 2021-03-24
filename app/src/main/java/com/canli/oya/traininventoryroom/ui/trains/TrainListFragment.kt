package com.canli.oya.traininventoryroom.ui.trains

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.LoadState
import androidx.paging.PagedList
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.BaseAdapter
import com.canli.oya.traininventoryroom.ui.base.BaseListFragment
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TrainListFragment : BaseListFragment<TrainMinimal>(), TrainItemClickListener,
    SwipeDeleteListener<TrainMinimal> {

    private lateinit var viewModel: TrainViewModel

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

    private var mTrainList: PagingData<TrainMinimal>? = null

    private var addMenuItem: MenuItem? = null

    private lateinit var intentRequest: String

    private var brandName: String? = null

    private var categoryName: String? = null

    override fun getListAdapter(): BaseAdapter<TrainMinimal, out Any> =
        TrainAdapter(requireContext(), this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentRequest = arguments?.getString(INTENT_REQUEST_CODE) ?: ALL_TRAIN
        if (intentRequest == TRAINS_OF_BRAND) {
            brandName = arguments?.getString(BRAND_NAME)
        }
        if (intentRequest == TRAINS_OF_CATEGORY) {
            categoryName = arguments?.getString(CATEGORY_NAME)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        evaluateResults(R.string.no_trains_found)
        binding.uiState = viewModel.listUiState

        when (intentRequest) {
            //If the fragment will be used for showing trains from a specific brand
            TRAINS_OF_BRAND -> {
                brandName?.let {
                    (activity as? MainActivity)?.supportActionBar?.title =
                        getString(R.string.trains_of_the_brand, it)
                    lifecycleScope.launch {
                        viewModel.getTrainsFromThisBrand(it).collectLatest { trainEntries ->
                            adapter.submitData(trainEntries)
                            mTrainList = trainEntries
                            evaluateResults(R.string.no_train_for_this_brand)
                        }
                    }
                }
            }
            //If the fragment_list will be used for showing trains from a specific category
            TRAINS_OF_CATEGORY -> {
                categoryName?.let {
                    (activity as? MainActivity)?.supportActionBar?.title =
                        getString(R.string.all_from_this_Category, it)
                    lifecycleScope.launch {
                        viewModel.getTrainsFromThisCategory(it).collectLatest { trainEntries ->
                            adapter.submitData(trainEntries)
                            mTrainList = trainEntries
                            evaluateResults(R.string.no_train_for_this_category)
                        }
                    }
                }
            }
            else -> {
                //If the fragment_list is going to be use for showing all trains, which is the default behaviour
                (activity as? MainActivity)?.supportActionBar?.title =
                    getString(R.string.all_trains)
                lifecycleScope.launch {
                    viewModel.allItems.collectLatest { trainEntries ->
                        adapter.submitData(trainEntries)
                        mTrainList = trainEntries
                        evaluateResults(R.string.no_train_for_this_brand)
                        R.string.no_trains_found
                    }
                }
            }
        }
    }

    private fun evaluateResults(@StringRes message: Int) {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                when(it.refresh) {
                    is LoadState.Loading -> {
                        viewModel.listUiState.showLoading = true
                    }
                    is LoadState.NotLoading -> {
                        viewModel.listUiState.showLoading = false
                        if(it.append.endOfPaginationReached && adapter.itemCount < 1){
                            viewModel.listUiState.showEmpty = true
                            viewModel.listUiState.emptyMessage = message
                        } else {
                            viewModel.listUiState.showList = true
                        }
                    }
                }
            }
        }
    }

    override fun onListItemClick(trainId: Int) {
        val action =
            TrainListFragmentDirections.actionTrainListFragmentToTrainDetailsFragment(trainId)
        binding.root.findNavController().navigate(action)
    }

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
                lifecycleScope.launch {
                    filterTrains(query)
                }
                return false
            }
        })
    }

    private suspend fun filterTrains(query: String?) {
        if (query.isNullOrBlank()) {
            mTrainList?.let {
                adapter.submitData(it)
            }
        } else {
            viewModel.searchInTrains(query).collect { filteredTrains ->
                adapter.submitData(filteredTrains)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    addMenuItem?.setIcon(R.drawable.avd_plus_to_save)
                    val anim = addMenuItem?.icon as? AnimatedVectorDrawable
                    anim?.start()
                }
                val action =
                    TrainListFragmentDirections.actionTrainListFragmentToAddTrainFragment(null)
                binding.root.findNavController().navigate(action)
            }
            R.id.export_to_excel -> NavigationUI.onNavDestinationSelected(
                item,
                binding.root.findNavController()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDeleteConfirmed(itemToDelete: TrainMinimal, position: Int) {
        viewModel.deleteTrain(itemToDelete.trainId)
        adapter.itemDeleted(position)
    }

    override fun onDeleteCanceled(position: Int) = adapter.cancelDelete(position)

    fun scrollToTop() {
        binding.list.smoothScrollToPosition(0)
    }
}
