package com.canli.oya.traininventoryroom.ui.trains

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.PagingData
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.BaseAdapter
import com.canli.oya.traininventoryroom.ui.base.BaseListFragment
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canli.oya.traininventoryroom.utils.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TrainListFragment : BaseListFragment<TrainMinimal>(), TrainItemClickListener,
    SwipeDeleteListener<TrainMinimal> {

    private lateinit var viewModel: TrainViewModel

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

    private var mTrainList: PagingData<TrainMinimal>? = null

    private var addMenuItem: MenuItem? = null


    override fun getListAdapter(): BaseAdapter<TrainMinimal, out Any> =
        TrainAdapter(requireContext(), this, this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        observeUIState(R.string.no_trains_found)
        lifecycleScope.launch {
            viewModel.allItems.collectLatest { trainEntries ->
                adapter.submitData(trainEntries)
                mTrainList = trainEntries
            }
        }
    }

    override fun onListItemClick(trainId: Int) {
        val action = TrainListFragmentDirections.actionTrainListFragmentToTrainDetailsFragment(trainId)
        binding.root.findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_and_add, menu)
        addMenuItem = menu.findItem(R.id.action_add)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    addMenuItem?.setIcon(R.drawable.avd_plus_to_save)
                    val anim = addMenuItem?.icon as? AnimatedVectorDrawable
                    anim?.start()
                }
                val action = TrainListFragmentDirections.actionTrainListFragmentToAddTrainFragment(null)
                binding.root.findNavController().navigate(action)
            }
            R.id.export_to_excel -> NavigationUI.onNavDestinationSelected(
                item, binding.root.findNavController()
            )
            R.id.action_search -> {
                val action = TrainListFragmentDirections.actionTrainListFragmentToFilterTrainFragment()
                binding.root.findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDeleteConfirmed(itemToDelete: TrainMinimal, position: Int) {
        viewModel.deleteTrain(itemToDelete.trainId)
        adapter.itemDeleted(position)
    }

    override fun onDeleteCanceled(position: Int) = adapter.cancelDelete(position)

    fun scrollToTop() {
        binding.list.smoothScrollToPosition(0)
    }
}
