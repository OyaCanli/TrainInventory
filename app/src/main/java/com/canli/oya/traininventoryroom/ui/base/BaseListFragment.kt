package com.canli.oya.traininventoryroom.ui.base

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.recyclerview.widget.ItemTouchHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.utils.SwipeToDeleteCallback
import com.canli.oya.traininventoryroom.utils.TITLE_CROSS
import com.canli.oya.traininventoryroom.utils.TITLE_PLUS
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseListFragment<T : Any> : Fragment(R.layout.fragment_list) {

    protected val binding by viewBinding(FragmentListBinding::bind)
    protected lateinit var adapter: BaseAdapter<T, out Any>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        adapter = getListAdapter()

        binding.list.adapter = adapter

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapter)).attachToRecyclerView(
            binding.list
        )
    }

    abstract fun getListAdapter(): BaseAdapter<T, out Any>

    protected fun blinkAddMenuItem(addMenuItem: MenuItem, @DrawableRes iconToSet: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            addMenuItem.setIcon(R.drawable.avd_blinking_plus)
            val blinkingAnim = addMenuItem.icon as? AnimatedVectorDrawable
            blinkingAnim?.clearAnimationCallbacks()
            blinkingAnim?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    addMenuItem.setMenuIcon(iconToSet)
                }
            })
            blinkingAnim?.start()
        }
    }

    fun MenuItem.setMenuIcon(@DrawableRes iconResource: Int) {
        setIcon(iconResource)
        title = if (iconResource == R.drawable.avd_plus_to_cross) TITLE_PLUS else TITLE_CROSS
    }

    protected fun observeUIState(@StringRes message: Int) {
        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest {
                Timber.d("New LoadState is received")
                when (it.refresh) {
                    is LoadState.Loading -> {
                        setUILoading()
                        Timber.d("state is Loading")
                    }
                    is LoadState.NotLoading -> {
                        Timber.d("state is Not-Loading")
                        if (adapter.itemCount < 1) {
                            setUIEmpty(message)
                            Timber.d("list is empty")
                        } else {
                            setUISuccess()
                            Timber.d("list is not empty")
                        }
                    }
                }
            }
        }
    }

    protected fun setUIEmpty(emptyMessage: Int) {
        binding.list.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        binding.emptyImage.visibility = View.VISIBLE
        binding.emptyText.setText(emptyMessage)
        binding.emptyText.visibility = View.VISIBLE
    }

    protected fun setUILoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.emptyImage.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
        binding.list.visibility = View.GONE
    }

    protected fun setUISuccess() {
        binding.list.visibility = View.VISIBLE
        binding.loadingIndicator.visibility = View.GONE
        binding.emptyImage.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
    }
}