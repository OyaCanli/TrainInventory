package com.canli.oya.traininventoryroom.ui.base

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.utils.SwipeToDeleteCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseListFragment<T> : Fragment(), CoroutineScope {

    protected lateinit var binding: FragmentListBinding
    protected lateinit var adapter : BaseAdapter<T, out Any>

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected lateinit var job : Job

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false)

        setHasOptionsMenu(true)

        job = Job()

        adapter = getListAdapter()

        binding.list.adapter = adapter

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapter)).attachToRecyclerView(binding.list)

        return binding.root
    }

    abstract fun getListAdapter() : BaseAdapter<T, out Any>

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

    protected fun blinkAddMenuItem(addMenuItem : MenuItem, @DrawableRes iconToSet : Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            addMenuItem.setIcon(R.drawable.avd_blinking_plus)
            val blinkingAnim = addMenuItem.icon as? AnimatedVectorDrawable
            blinkingAnim?.clearAnimationCallbacks()
            blinkingAnim?.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {}

                override fun onAnimationEnd(drawable: Drawable) {
                    addMenuItem.setIcon(iconToSet)
                }
            })
            blinkingAnim?.start()
        }
    }
}