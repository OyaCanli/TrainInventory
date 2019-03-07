package com.canli.oya.traininventoryroom.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.view.animation.AnimationUtils
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.TrainAdapter
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel

class TrainListFragment : Fragment(), TrainAdapter.TrainItemClickListener {

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

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
        binding.list.layoutManager = LinearLayoutManager(activity)
        binding.list.itemAnimator = DefaultItemAnimator()
        binding.list.adapter = mAdapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel.loadTrainList(InjectorUtils.provideTrainRepo(requireContext()))

        val bundle = arguments
        //If the list will be used for showing selected trains
        if (bundle != null && bundle.containsKey(INTENT_REQUEST_CODE)) {
            val requestType = bundle.getString(INTENT_REQUEST_CODE)
            when (requestType) {
                TRAINS_OF_BRAND -> {
                    val brandName = bundle.getString(BRAND_NAME)
                    activity!!.title = getString(R.string.trains_of_the_brand, brandName)
                    mViewModel.getTrainsFromThisBrand(brandName).observe(this@TrainListFragment, Observer { trainEntries ->
                        if (trainEntries == null || trainEntries.isEmpty()) {
                            binding.isEmpty = true
                            binding.emptyMessage = getString(R.string.no_train_for_this_brand)
                            animateTrainLogo()
                        } else {
                            binding.isEmpty = false
                            mAdapter.trainList = trainEntries
                            mTrainList = trainEntries
                        }
                    })
                }
                TRAINS_OF_CATEGORY -> {
                    val categoryName = bundle.getString(CATEGORY_NAME)
                    activity!!.title = getString(R.string.all_from_this_Category, categoryName)
                    mViewModel.getTrainsFromThisCategory(categoryName).observe(this@TrainListFragment, Observer { trainEntries ->
                        if (trainEntries.isNullOrEmpty()) {
                            binding.isEmpty = true
                            binding.emptyMessage = getString(R.string.no_items_for_this_category)
                            animateTrainLogo()
                        } else {
                            binding.isEmpty = false
                            mAdapter.trainList = trainEntries
                            mTrainList = trainEntries
                        }
                    })
                }
                else -> {
                    //If the list is going to be use for showing all trains, which is the default behaviour
                    activity!!.title = getString(R.string.all_trains)
                    mViewModel.trainList?.observe(this@TrainListFragment, Observer { trainEntries ->
                        if (trainEntries.isNullOrEmpty()) {
                            binding.isEmpty = true
                            binding.emptyMessage = getString(R.string.no_trains_found)
                            animateTrainLogo()
                        } else {
                            binding.isEmpty = false
                            mAdapter.trainList = trainEntries
                            mTrainList = trainEntries
                        }
                    })
                }
            }
        }
    }

    override fun onListItemClick(trainId: Int) {
        val trainDetailsFrag = TrainDetailsFragment()
        val args = Bundle()
        args.putInt(TRAIN_ID, trainId)
        trainDetailsFrag.arguments = args
        val fm = fragmentManager
        fm!!.beginTransaction()
                .replace(R.id.container, trainDetailsFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_search_and_add, menu)
        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        //added filter to list (dynamic change input text)
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
            AppExecutors.instance.diskIO().execute {
                filteredTrains = mViewModel.searchInTrains(query)
                activity!!.runOnUiThread {
                    mAdapter.trainList = filteredTrains
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_add) {
            openAddTrainFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAddTrainFragment() {
        val addTrainFragment = AddTrainFragment()
        val fm = fragmentManager
        fm!!.beginTransaction()
                .replace(R.id.container, addTrainFragment)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit()
    }

    private fun animateTrainLogo() {
        val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
        binding.emptyImage.startAnimation(animation)
    }

    fun scrollToTop() {
        binding.list.smoothScrollToPosition(0)
    }

}
