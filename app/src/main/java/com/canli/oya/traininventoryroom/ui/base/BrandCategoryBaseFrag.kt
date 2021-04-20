package com.canli.oya.traininventoryroom.ui.base

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.utils.IS_EDIT
import com.canli.oya.traininventoryroom.utils.SwipeToDeleteCallback
import com.canli.oya.traininventoryroom.utils.clearFocusAndHideKeyboard
import com.canli.oya.traininventoryroom.utils.shortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class BrandCategoryBaseFrag<T : Any> : BaseListFragment<T>(), SwipeDeleteListener<T> {

    protected lateinit var viewModel: BrandCategoryBaseVM<T>

    var addMenuItem: MenuItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getListViewModel()

        observeUIState(viewModel.emptyMessage)

        lifecycleScope.launch {
            viewModel.allPagedItems.collectLatest { brandEntries ->
                Timber.d("Brand entries received.")
                adapter.submitData(brandEntries)
            }
        }

        activity?.title = getTitle()

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapter)).attachToRecyclerView(
            binding.list
        )
    }

    abstract fun getListViewModel(): BrandCategoryBaseVM<T>

    abstract fun getTitle(): String

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
        addMenuItem = menu.getItem(0)
        if (viewModel.isChildFragVisible) {
            addMenuItem?.setMenuIcon((R.drawable.avd_cross_to_plus))
        } else {
            addMenuItem?.setMenuIcon((R.drawable.avd_plus_to_cross))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> onAddClicked(item)
            R.id.export_to_excel -> NavigationUI.onNavDestinationSelected(
                item,
                binding.root.findNavController()
            )
            R.id.trashFragment -> NavigationUI.onNavDestinationSelected(
                item, binding.root.findNavController()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAddClicked(item: MenuItem) {
        if (viewModel.isChildFragVisible) {
            removeChildFragment()
            activity?.clearFocusAndHideKeyboard()
            startAnimationOnMenuItem(
                item,
                R.drawable.avd_cross_to_plus,
                R.drawable.avd_plus_to_cross
            )
        } else {
            openChildFragment(getChildFragment())
            startAnimationOnMenuItem(
                item,
                R.drawable.avd_plus_to_cross,
                R.drawable.avd_cross_to_plus
            )
        }
        viewModel.isChildFragVisible = !viewModel.isChildFragVisible
    }

    private fun clearFocusAndHideKeyboard() {
        //This is for closing soft keyboard if user navigates to another fragment while keyboard was open
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = activity?.currentFocus
        focusedView?.run {
            clearFocus()
            imm.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }

    private fun startAnimationOnMenuItem(
        item: MenuItem,
        @DrawableRes iconAtStart: Int,
        @DrawableRes iconAtEnd: Int
    ) {
        if (Build.VERSION.SDK_INT >= 23) {
            //If there is an ongoing animation, cancel it
            val previousAvd = item.icon as? AnimatedVectorDrawable
            previousAvd?.clearAnimationCallbacks()

            //In case the drawable is different(i.e. blinking animation), set the correct starting icon
            item.setMenuIcon(iconAtStart)
            val avd = item.icon as? AnimatedVectorDrawable
            avd?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    item.setMenuIcon(iconAtEnd)
                }
            })
            avd?.start()
        } else {
            item.setMenuIcon(iconAtEnd)
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
        args.putBoolean(IS_EDIT, true)
        childFrag.arguments = args
        viewModel.isChildFragVisible = true
        addMenuItem?.let {
            startAnimationOnMenuItem(it, R.drawable.avd_plus_to_cross, R.drawable.avd_cross_to_plus)
        }
        openChildFragment(childFrag)
    }

    abstract fun getChildFragment(): Fragment

    override fun onDeleteConfirmed(itemToDelete: T, position: Int) {
        Timber.d("delete is confirmed")
        lifecycleScope.launch {
            //First check whether this item is used by trains table
            val isActivelyUsed = withContext(Dispatchers.IO) { viewModel.isThisItemUsed(itemToDelete) }
            val isUsedInTrashedItems = withContext(Dispatchers.IO) { viewModel.isThisItemUsedInTrash(itemToDelete) }
            if (isActivelyUsed) {
                // If it is used, show a warning and don't let user delete this
                context?.shortToast(R.string.cannot_erase_category)
                adapter.cancelDelete(position)
            } else if(isUsedInTrashedItems) {
                val builder = AlertDialog.Builder(requireActivity(), R.style.alert_dialog_style)
                with(builder) {
                    setMessage(getString(R.string.category_used_by_trains_in_trash))
                    setPositiveButton(R.string.yes_delete) { _, _ ->
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                viewModel.deleteTrainsInTrashWithThisItem(itemToDelete)
                                viewModel.deleteItem(itemToDelete)
                            }
                            adapter.itemDeleted(position)
                        }
                    }
                    setNegativeButton(R.string.cancel) { _, _ ->
                        adapter.cancelDelete(position)
                    }
                    create()
                    show()
                }
            } else {
                //If it is not used, erase the item
                viewModel.deleteItem(itemToDelete)
                adapter.itemDeleted(position)
            }
        }
    }

    override fun onDeleteCanceled(position: Int) = adapter.cancelDelete(position)

}
