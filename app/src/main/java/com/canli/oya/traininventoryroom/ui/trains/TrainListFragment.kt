package com.canli.oya.traininventoryroom.ui.trains

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.*
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class TrainListFragment : Fragment(), TrainAdapter.TrainItemClickListener, CoroutineScope {

    private val mViewModel by viewModels<TrainViewModel>()

    private lateinit var trainListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + trainListJob

    private lateinit var binding: FragmentListBinding
    private lateinit var mAdapter: TrainAdapter
    private var mTrainList: PagedList<TrainMinimal>? = null

    private var addMenuItem: MenuItem? = null

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
                //If the fragment will be used for showing trains from a specific brand
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
                    mViewModel.trainList.observe(this@TrainListFragment, Observer { trainEntries ->
                        evaluateResults(trainEntries, getString(R.string.no_trains_found), true)
                    })
                }
            }
        }
    }

    private fun evaluateResults(trainEntries: PagedList<TrainMinimal>?, message: String, noTrain : Boolean = false) {
        if (trainEntries.isNullOrEmpty()) {
            mViewModel.trainListUiState.emptyMessage = message
            mViewModel.trainListUiState.showEmpty = true
            animateTrainLogo()
            if(noTrain) {
                blinkAddMenuItem()
            }
        } else {
            mAdapter.submitList(trainEntries)
            mTrainList = trainEntries
            mViewModel.trainListUiState.showList = true
        }
    }

    private fun blinkAddMenuItem() {
        if (Build.VERSION.SDK_INT >= 23) {
            val blinkingAnim = addMenuItem?.icon as AnimatedVectorDrawable
            blinkingAnim.clearAnimationCallbacks()
            blinkingAnim.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    blinkingAnim.reset()
                }
            })
            blinkingAnim.start()
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
            mViewModel.searchInTrains(query).observe(this, Observer { filteredTrains ->
                if(filteredTrains.isNotEmpty()){
                    mAdapter.submitList(filteredTrains)
                    mViewModel.searchInTrains(query).removeObservers(this)
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            if (Build.VERSION.SDK_INT >= 23) {
                addMenuItem?.setIcon(R.drawable.avd_plus_to_save)
                val anim = addMenuItem?.icon as AnimatedVectorDrawable
                /*anim.clearAnimationCallbacks()
                anim.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationStart(drawable: Drawable) {}
                    override fun onAnimationEnd(drawable: Drawable) {
                        //openAddTrainFragment()
                    }
                })*/
                anim.start()
            }
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
}
