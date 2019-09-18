package com.canli.oya.traininventoryroom.ui

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.TrainAdapter
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class TrainListFragment : Fragment(), TrainAdapter.TrainItemClickListener, CoroutineScope {

    private val mViewModel by activityViewModels<MainViewModel>()

    private lateinit var trainListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + trainListJob

    private lateinit var binding: FragmentListBinding
    private lateinit var mAdapter: TrainAdapter
    private var mTrainList: List<TrainMinimal> = emptyList()
    private var filteredTrains: List<TrainMinimal> = emptyList()

    private val disposable = CompositeDisposable()

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
            when (this.getString(INTENT_REQUEST_CODE)) {
                //If the fragment_list will be used for showing trains from a specific brand
                TRAINS_OF_BRAND -> {
                    val brandName = this.getString(BRAND_NAME) ?: return
                    activity?.title = getString(R.string.trains_of_the_brand, brandName)
                    disposable.add(mViewModel.getTrainsFromThisBrand(brandName).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    //onNext
                                    { trainEntries ->
                                        evaluateResults(trainEntries, getString(R.string.no_train_for_this_brand))
                                    },
                                    // onError
                                    {error ->
                                        Timber.e("Unable to get trains for this brand ${error.message}")
                                    }
                            )
                    )
                }
                //If the fragment_list will be used for showing trains from a specific category
                TRAINS_OF_CATEGORY -> {
                    val categoryName = this.getString(CATEGORY_NAME) ?: return
                    activity?.title = getString(R.string.all_from_this_Category, categoryName)
                    disposable.add(mViewModel.getTrainsFromThisCategory(categoryName).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    //onNext
                                    { trainEntries ->
                                        evaluateResults(trainEntries, getString(R.string.no_train_for_this_category))
                                    },
                                    // onError
                                    { error ->
                                        Timber.e("Unable to get trains for this category ${error.message}")
                                    }
                            )
                    )
                }
                else -> {
                    //If the fragment_list is going to be use for showing all trains, which is the default behaviour
                    activity?.title = getString(R.string.all_trains)
                    disposable.add(mViewModel.trainList
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    //onNext
                                    { trainEntries ->
                                        evaluateResults(trainEntries, getString(R.string.no_trains_found))
                                    },
                                    // onError
                                    { error ->
                                        Timber.e("Unable to get train list ${error.message}")
                                    }
                            )
                    )
                }
            }
        }
    }


    private fun evaluateResults(trainEntries: List<TrainMinimal>?, message: String) {
        if (trainEntries.isNullOrEmpty()) {
            mViewModel.trainListUiState.emptyMessage = message
            mViewModel.trainListUiState.showEmpty = true
            animateTrainLogo()
        } else {
            mAdapter.submitList(trainEntries)
            mTrainList = trainEntries
            mViewModel.trainListUiState.showList = true
        }
    }

    override fun onListItemClick(trainId: Int) {
        val trainDetailsFrag = TrainDetailsFragment()
        val args = Bundle()
        args.putInt(TRAIN_ID, trainId)
        trainDetailsFrag.arguments = args
        fragmentManager?.commit {
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
            mAdapter.submitList(filteredTrains)
            mAdapter.notifyDataSetChanged()
        } else {
            launch {
                filteredTrains = mViewModel.searchInTrains(query)
                mAdapter.submitList(filteredTrains)
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
        fragmentManager?.commit {
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

    override fun onStop() {
        super.onStop()
        // clear all the subscription
        disposable.clear()
    }

}
