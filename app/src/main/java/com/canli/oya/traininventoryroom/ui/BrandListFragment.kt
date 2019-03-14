package com.canli.oya.traininventoryroom.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.BrandAdapter
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.BrandCategoryList
import com.canli.oya.traininventoryroom.utils.BRAND_NAME
import com.canli.oya.traininventoryroom.utils.EDIT_CASE
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.TRAINS_OF_BRAND
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import kotlinx.coroutines.*
import org.jetbrains.anko.design.indefiniteSnackbar
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

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list_with_framelayout, container, false)

        setHasOptionsMenu(true)

        brandListJob = Job()

        mAdapter = BrandAdapter(this)
        with(binding.included.list){
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.included.uiState = mViewModel.brandListUiState

        mViewModel.brandList?.observe(this@BrandListFragment, Observer { brandEntries ->
            if (brandEntries.isNullOrEmpty()) {
                mViewModel.brandListUiState.showEmpty = true
                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.included.emptyImage.startAnimation(animation)
            } else {
                Timber.d("list size : ${brandEntries.size}")
                mAdapter.brandList = brandEntries
                brands = brandEntries
                mViewModel.brandListUiState.showList = true
            }
        })
        activity?.title = getString(R.string.all_brands)

        val coordinator = activity?.findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.coordinator)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //First take a backup of the brand to erase
                val brandToErase = brands[position]

                launch {
                    //Check whether this brand is used in trains table.
                    val isUsed = withContext(Dispatchers.IO) { mViewModel.isThisBrandUsed(brandToErase.brandName)}
                    Timber.d("isUsed : $isUsed")
                    if (isUsed) {
                        // If it is used, show a warning and don't let the user delete this
                        context?.toast(R.string.cannot_erase_brand)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        //If it is not used delete the brand
                        mViewModel.deleteBrand(brandToErase)
                        //Show a snack bar for undoing delete
                        coordinator?.indefiniteSnackbar(R.string.brand_deleted, R.string.brand_deleted) {
                            mViewModel.insertBrand(brandToErase) }
                    }
                }
            }
        }).attachToRecyclerView(binding.included.list)
    }

    private fun openAddBrandFragment() {
        val addBrandFrag = AddBrandFragment()
        childFragmentManager.transaction {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.brandlist_addFrag_container, addBrandFrag)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            openAddBrandFragment()
        }
        return super.onOptionsItemSelected(item)
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
        childFragmentManager.transaction {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.brandlist_addFrag_container, addBrandFrag)
        }
    }

    private fun showTrainsFromThisBrand(clickedBrand: BrandEntry) {
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
        args.putString(BRAND_NAME, clickedBrand.brandName)
        trainListFrag.arguments = args
        fragmentManager?.transaction {
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        brandListJob.cancel()
    }
}

