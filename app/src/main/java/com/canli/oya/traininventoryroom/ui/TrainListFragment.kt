package com.canli.oya.traininventoryroom.ui

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.TrainAdapter
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TrainListFragment : Fragment(), TrainAdapter.TrainItemClickListener, CoroutineScope {

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    private lateinit var trainListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + trainListJob

    private lateinit var binding: FragmentListBinding
    private lateinit var mAdapter: TrainAdapter
    private var mTrainList: List<TrainEntry> = emptyList()
    private var filteredTrains: List<TrainEntry> = emptyList()

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false)
        setHasOptionsMenu(true)

        //Set recycler view
        mAdapter = TrainAdapter(this)
        val divider = DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL)
        divider.setDrawable(ShapeDrawable().apply {
            intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.divider_height)
            paint.color = resources.getColor(R.color.divider_color)
        })

        with(binding.list) {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(divider)
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.uiState = mViewModel.trainListUiState

        arguments?.run {
            val requestType = this.getString(INTENT_REQUEST_CODE)
            when (requestType) {
                //If the fragment_list will be used for showing trains from a specific brand
                TRAINS_OF_BRAND -> {
                    val brandName = this.getString(BRAND_NAME) ?: return
                    activity?.title = getString(R.string.trains_of_the_brand, brandName)
                    mViewModel.getTrainsFromThisBrand(brandName).observe(this@TrainListFragment, Observer { trainEntries ->
                        evaluateResults(trainEntries, getString(R.string.no_train_for_this_brand))
                    })
                }
                //If the fragment_list will be used for showing trains from a specific category
                TRAINS_OF_CATEGORY -> {
                    val categoryName = this.getString(CATEGORY_NAME) ?: return
                    activity?.title = getString(R.string.all_from_this_Category, categoryName)
                    mViewModel.getTrainsFromThisCategory(categoryName).observe(this@TrainListFragment, Observer { trainEntries ->
                        evaluateResults(trainEntries, getString(R.string.no_train_for_this_category))
                    })
                }
                else -> {
                    //If the fragment_list is going to be use for showing all trains, which is the default behaviour
                    activity?.title = getString(R.string.all_trains)
                    mViewModel.trainList?.observe(this@TrainListFragment, Observer { trainEntries ->
                        evaluateResults(trainEntries, getString(R.string.no_trains_found))
                    })
                }
            }
        }
    }


    private fun evaluateResults(trainEntries: List<TrainEntry>?, message: String) {
        if (trainEntries.isNullOrEmpty()) {
            mViewModel.trainListUiState.emptyMessage = message
            mViewModel.trainListUiState.showEmpty = true
            animateTrainLogo()
        } else {
            mAdapter.trainList = trainEntries
            mTrainList = trainEntries
            mViewModel.trainListUiState.showList = true
        }
    }

    override fun onListItemClick(trainId: Int) {
        val trainDetailsFrag = TrainDetailsFragment()
        val args = Bundle()
        args.putInt(TRAIN_ID, trainId)
        trainDetailsFrag.arguments = args
        fragmentManager?.transaction {
            replace(R.id.container, trainDetailsFrag)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .addToBackStack(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_and_add, menu)
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
        if (query == null || "" == query) {
            filteredTrains = mTrainList
            mAdapter.trainList = filteredTrains
            mAdapter.notifyDataSetChanged()
        } else {
            launch {
                filteredTrains = mViewModel.searchInTrains(query)
                mAdapter.trainList = filteredTrains
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            openAddTrainFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAddTrainFragment() {
        val addTrainFrag = AddTrainFragment()
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        addTrainFrag.arguments = args
        fragmentManager?.transaction {
            replace(R.id.container, addTrainFrag)
            setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            addToBackStack(null)
        }
    }

    private fun animateTrainLogo() {
        val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
        binding.emptyImage.startAnimation(animation)
    }

    fun scrollToTop() {
        binding.list.smoothScrollToPosition(0)
    }

}
