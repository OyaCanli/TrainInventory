package com.canli.oya.traininventoryroom.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.adapters.BrandAdapter
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.databinding.FragmentBrandlistBinding
import com.canli.oya.traininventoryroom.utils.*
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class BrandListFragment : androidx.fragment.app.Fragment(), BrandAdapter.BrandItemClickListener {

    private var brands: List<BrandEntry>? = null
    private var adapter: BrandAdapter? = null

    private lateinit var binding: FragmentBrandlistBinding

    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_brandlist, container, false)

        setHasOptionsMenu(true)

        adapter = BrandAdapter(this)
        binding.included.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        binding.included.list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.included.list.adapter = adapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel.loadBrandList(InjectorUtils.provideBrandRepo(requireContext()))
        mViewModel.brandList?.observe(this@BrandListFragment, Observer { brandEntries ->
            if (brandEntries.isNullOrEmpty()) {
                binding.included.isEmpty = true
                binding.included.emptyMessage = getString(R.string.no_brands_found)
                val animation = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.included.emptyImage.startAnimation(animation)
            } else {
                adapter?.brandList = brandEntries
                brands = brandEntries
                binding.included.isEmpty = false
            }
        })
        activity!!.title = getString(R.string.all_brands)

        val coordinator = activity!!.findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.coordinator)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //First take a backup of the brand to erase
                val brandToErase = brands?.get(position)

                AppExecutors.instance.diskIO().execute {
                    //Check whether this brand is used in trains table.
                    if (mViewModel.isThisBrandUsed(brandToErase?.brandName!!)) {
                        // If it is used, show a warning and don't let the user delete this
                        activity!!.runOnUiThread {
                            Toast.makeText(activity, R.string.cannot_erase_brand, Toast.LENGTH_LONG).show()
                            adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        //If it is not used delete the brand
                        mViewModel.deleteBrand(brandToErase)

                        //Show a snack bar for undoing delete
                        val snackbar = Snackbar
                                .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.undo) { mViewModel.insertBrand(brandToErase) }
                        snackbar.show()
                    }
                }
            }
        }).attachToRecyclerView(binding.included.list)
    }

    private fun openAddBrandFragment() {
        val addBrandFrag = AddBrandFragment()
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit()
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
            R.id.brand_item_web_icon -> {
                openWebSite(clickedBrand)
            }
            R.id.brand_item_train_icon -> {
                showTrainsFromThisBrand(clickedBrand)
            }
            R.id.brand_item_edit_icon -> {
                editBrand(clickedBrand)
            }
        }
    }

    private fun editBrand(clickedBrand: BrandEntry) {
        mViewModel.setChosenBrand(clickedBrand)
        val addBrandFrag = AddBrandFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, EDIT_CASE)
        addBrandFrag.arguments = args
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addBrandFrag)
                .commit()
    }

    private fun showTrainsFromThisBrand(clickedBrand: BrandEntry) {
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
        args.putString(BRAND_NAME, clickedBrand.brandName)
        trainListFrag.arguments = args
        fragmentManager!!.beginTransaction()
                .replace(R.id.container, trainListFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .commit()
    }

    private fun openWebSite(clickedBrand: BrandEntry) {
        val urlString = clickedBrand.webUrl
        var webUri: Uri? = null
        if (!TextUtils.isEmpty(urlString)) {
            try {
                webUri = Uri.parse(urlString)
            } catch (e: Exception) {
                Log.e("BrandListFragment", e.toString())
            }

            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = webUri
            if (webIntent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(webIntent)
            }
        }
    }
}

