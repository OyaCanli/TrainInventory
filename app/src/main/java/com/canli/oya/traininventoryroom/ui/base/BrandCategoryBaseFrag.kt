package com.canli.oya.traininventoryroom.ui.base

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.utils.EDIT_CASE
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.SwipeToDeleteCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import timber.log.Timber

abstract class BrandCategoryBaseFrag<T> : BaseListFragment<T>(), SwipeDeleteListener<T> {

    protected lateinit var viewModel : BrandCategoryBaseVM<T>

    var addMenuItem: MenuItem? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = getListViewModel()

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
                viewModel.listUiState.showList = true
            }
        })

        activity?.title = getTitle()

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapter)).attachToRecyclerView(binding.list)
    }

    abstract fun getListViewModel() : BrandCategoryBaseVM<T>

    abstract fun getTitle() : String

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
                removeChildFragment()
                viewModel.isChildFragVisible = false
                R.drawable.avd_plus_to_cross
            } else {
                openChildFragment(getChildFragment())
                viewModel.isChildFragVisible = true
                R.drawable.avd_cross_to_plus
            }
            startAnimationOnMenuItem(item, icon)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun startAnimationOnMenuItem(item: MenuItem, @DrawableRes iconRes : Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val avd = item.icon as? AnimatedVectorDrawable
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

    private fun removeChildFragment() {
        val childFrag = childFragmentManager.findFragmentById(R.id.list_addFrag_container)
        childFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            childFrag?.let { remove(it) }
        }
    }

    private fun openChildFragment(frag: Fragment) {
        childFragmentManager.commit {
            setCustomAnimations(R.anim.translate_from_top, 0)
                    .replace(R.id.list_addFrag_container, frag)
        }
    }

    protected fun editItem(clickedItem: T) {
        viewModel.setChosenItem(clickedItem)
        val childFrag = getChildFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, EDIT_CASE)
        childFrag.arguments = args
        viewModel.isChildFragVisible = true
        addMenuItem?.let {
            startAnimationOnMenuItem(addMenuItem!!, R.drawable.avd_cross_to_plus)
        }
        openChildFragment(childFrag)
    }

    abstract fun getChildFragment() : Fragment

    override fun onDeleteConfirmed(itemToDelete: T, position : Int) {
        Timber.d("delete is confirmed")
        launch {
            //First check whether this item is used by trains table
            val isUsed = withContext(Dispatchers.IO) { viewModel.isThisItemUsed(itemToDelete) }
            if (isUsed) {
                // If it is used, show a warning and don't let user delete this
                context?.toast(R.string.cannot_erase_category)
                adapter.cancelDelete(position)
            } else {
                //If it is not used, erase the item
                viewModel.deleteItem(itemToDelete)
                adapter.itemDeleted(position)
            }
        }
    }

    override fun onDeleteCanceled(position: Int) = adapter.cancelDelete(position)

}