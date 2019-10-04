package com.canli.oya.traininventoryroom.ui.brands

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.BrandCategoryList
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import com.canli.oya.traininventoryroom.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.toast
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class BrandListFragment : Fragment(), BrandAdapter.BrandItemClickListener, CoroutineScope {

    private lateinit var brandListJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + brandListJob

    private lateinit var brands: List<BrandEntry>
    private lateinit var mAdapter: BrandAdapter

    private lateinit var binding: BrandCategoryList

    private val disposable = CompositeDisposable()

    private val mViewModel by viewModels<BrandViewModel>()

    private var addMenuItem : MenuItem? = null

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list_with_framelayout, container, false)

        setHasOptionsMenu(true)

        brandListJob = Job()

        mAdapter = BrandAdapter(this)
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

        binding.includedList.uiState = mViewModel.brandListUiState

        disposable.add(mViewModel.brandList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        { brandEntries ->
                            if (brandEntries.isNullOrEmpty()) {
                                mViewModel.brandListUiState.showEmpty = true
                                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                                binding.includedList.emptyImage.startAnimation(animation)
                            } else {
                                Timber.d("fragment_list size : ${brandEntries.size}")
                                mAdapter.submitList(brandEntries)
                                brands = brandEntries
                                mViewModel.brandListUiState.showList = true
                            }
                        },
                        // onError
                        { error ->
                            Timber.e("Unable to get brand list ${error.message}")
                        }
                ))

        activity?.title = getString(R.string.all_brands)

        val rootView = activity!!.findViewById<FrameLayout>(R.id.container)
        ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //First take a backup of the brand to erase
                val brandToErase = brands[position]

                launch {
                    //Check whether this brand is used in trains table.
                    val isUsed = withContext(Dispatchers.IO) { mViewModel.isThisBrandUsed(brandToErase.brandName) }
                    if (isUsed) {
                        // If it is used, show a warning and don't let the user delete this
                        context.toast(R.string.cannot_erase_brand)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        //If it is not used delete the brand
                        mViewModel.deleteBrand(brandToErase)
                        //Show a snack bar for undoing delete
                        rootView?.longSnackbar(R.string.brand_deleted, R.string.undo) {
                            mViewModel.insertBrand(brandToErase)
                        }
                    }
                }
            }
        }).attachToRecyclerView(binding.includedList.list)

        mViewModel.isChildFragVisible.observe(this, Observer {
            if(it) {
                addMenuItem?.setIcon(R.drawable.ic_cancel)
            } else {
                addMenuItem?.setIcon(R.drawable.ic_add_light)
            }
        })
    }

    private fun openAddBrandFragment() {
        val addBrandFrag = AddBrandFragment()
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, addBrandFrag)
        }
        mViewModel.setIsChildFragVisible(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
        addMenuItem = menu.getItem(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            if(mViewModel.isChildFragVisible.value == false) {
                openAddBrandFragment()
            } else {
                removeAddBrandFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeAddBrandFragment() {
        val childFrag = childFragmentManager.findFragmentById(R.id.list_addFrag_container)
        childFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            childFrag?.let { remove(it) }
        }
        mViewModel.setIsChildFragVisible(false)
    }

    override fun onBrandItemClicked(view: View, clickedBrand: BrandEntry) {
        when (view.id) {
            R.id.brand_item_web_icon -> openWebSite(clickedBrand)
            R.id.brand_item_train_icon -> showTrainsFromThisBrand(clickedBrand)
            R.id.brand_item_edit_icon -> editBrand(clickedBrand)
        }
    }

    private fun editBrand(clickedBrand: BrandEntry) {
        mViewModel.setChosenBrand(clickedBrand)
        val addBrandFrag = AddBrandFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, EDIT_CASE)
        addBrandFrag.arguments = args
        mViewModel.setIsChildFragVisible(true)
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, addBrandFrag)
        }

    }


    private fun showTrainsFromThisBrand(clickedBrand: BrandEntry) {
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
        args.putString(BRAND_NAME, clickedBrand.brandName)
        trainListFrag.arguments = args
        fragmentManager?.commit {
            replace(R.id.container, trainListFrag)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .addToBackStack(null)
        }
    }

    private fun openWebSite(clickedBrand: BrandEntry) {
        val urlString = clickedBrand.webUrl
        var webUri: Uri? = null
        if (!TextUtils.isEmpty(urlString)) {
            try {
                webUri = Uri.parse(urlString)
            } catch (e: Exception) {
                Timber.e(e.toString())
            }

            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = webUri
            if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(webIntent)
            }
        } else {
            context?.toast(getString(R.string.no_website_warning))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        brandListJob.cancel()
    }

    override fun onStop() {
        super.onStop()
        // clear all the subscription
        disposable.clear()
    }
}

