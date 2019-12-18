package com.canli.oya.traininventoryroom.ui.brands

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.BaseAdapter
import com.canli.oya.traininventoryroom.common.EDIT_CASE
import com.canli.oya.traininventoryroom.common.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.common.SwipeDeleteListener
import com.canli.oya.traininventoryroom.data.BrandEntry
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.Navigator
import com.canli.oya.traininventoryroom.ui.base.BaseListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject

class BrandListFragment : BaseListFragment<BrandEntry>(), BrandItemClickListener, SwipeDeleteListener<BrandEntry> {

    private lateinit var brands: List<BrandEntry>

    private lateinit var viewModel : BrandViewModel

    @Inject
    lateinit var navigator : Navigator

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private var addMenuItem: MenuItem? = null

    override fun getListAdapter(): BaseAdapter<BrandEntry, BrandItemClickListener> = BrandAdapter(context!!, this, this)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity?.application as TrainApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(BrandViewModel::class.java)

        binding.uiState = viewModel.listUiState

        viewModel.allItems.observe(viewLifecycleOwner, Observer { brandEntries ->
            if (brandEntries.isNullOrEmpty()) {
                viewModel.listUiState.showEmpty = true
                val slideAnim = AnimationUtils.loadAnimation(activity, R.anim.translate_from_left)
                binding.emptyImage.startAnimation(slideAnim)
                //If there are no items and add is not clicked, blink add button to draw user's attention
                if(!viewModel.isChildFragVisible) {
                    addMenuItem?.let { blinkAddMenuItem(it, R.drawable.avd_plus_to_cross) }
                }
            } else {
                Timber.d("fragment_list size : ${brandEntries.size}")
                adapter.submitList(brandEntries)
                brands = brandEntries
                viewModel.listUiState.showList = true
            }
        })

        activity?.title = getString(R.string.all_brands)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
        addMenuItem = menu.getItem(0)
        if(viewModel.isChildFragVisible){
            addMenuItem?.setIcon((R.drawable.avd_cross_to_plus))
        } else {
            addMenuItem?.setIcon((R.drawable.avd_plus_to_cross))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            val icon : Int = if (viewModel.isChildFragVisible) {
                removeAddBrandFragment()
                R.drawable.avd_plus_to_cross
            } else {
                openAddBrandFragment()
                R.drawable.avd_cross_to_plus
            }
            viewModel.isChildFragVisible = !viewModel.isChildFragVisible
            startAnimationOnMenuItem(item, icon)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startAnimationOnMenuItem(item: MenuItem?, @DrawableRes iconRes : Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val avd = item?.icon as? AnimatedVectorDrawable
            avd?.clearAnimationCallbacks()
            avd?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    item.setIcon(iconRes)
                }
            })
            avd?.start()
        }
    }

    private fun openAddBrandFragment() {
        val addBrandFrag = AddBrandFragment()
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, addBrandFrag)
        }
    }

    private fun removeAddBrandFragment() {
        val childFrag = childFragmentManager.findFragmentById(R.id.list_addFrag_container)
        childFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            childFrag?.let { remove(it) }
        }
    }

    override fun onBrandItemClicked(view: View, clickedBrand: BrandEntry) {
        when (view.id) {
            R.id.brand_item_web_icon -> openWebSite(clickedBrand)
            R.id.brand_item_train_icon -> navigator.launchTrainList_withThisBrand(clickedBrand.brandName)
            R.id.brand_item_edit_icon -> editBrand(clickedBrand)
        }
    }

    override fun onDeleteConfirmed(itemToDelete: BrandEntry, position: Int) {
        launch {
            //Check whether this brand is used in trains table.
            val isUsed = withContext(Dispatchers.IO) { viewModel.isThisItemUsed(itemToDelete) }
            if (isUsed) {
                // If it is used, show a warning and don't let the user delete this
                context?.toast(R.string.cannot_erase_brand)
                adapter.notifyItemChanged(position)
            } else {
                //If it is not used delete the brand
                viewModel.deleteItem(itemToDelete)
                adapter.itemDeleted(position)
            }
        }
    }

    override fun onDeleteCanceled(position: Int) = adapter.cancelDelete(position)

    private fun editBrand(clickedBrand: BrandEntry) {
        viewModel.setChosenItem(clickedBrand)
        val addBrandFrag = AddBrandFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, EDIT_CASE)
        addBrandFrag.arguments = args
        viewModel.isChildFragVisible = true
        addMenuItem?.let {
            startAnimationOnMenuItem(addMenuItem!!, R.drawable.avd_cross_to_plus)
        }
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, addBrandFrag)
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
}

