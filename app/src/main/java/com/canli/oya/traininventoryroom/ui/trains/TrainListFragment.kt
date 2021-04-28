package com.canli.oya.traininventoryroom.ui.trains

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainMinimal
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.SwipeDeleteListener
import com.canli.oya.traininventoryroom.ui.common.SwipeToDeleteCallback
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener
import com.canli.oya.traininventoryroom.utils.showEmpty
import com.canli.oya.traininventoryroom.utils.showList
import com.canli.oya.traininventoryroom.utils.showLoading
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TrainListFragment : Fragment(R.layout.fragment_list), TrainItemClickListener, SwipeDeleteListener<TrainMinimal> {

    private lateinit var viewModel: TrainViewModel

    private val binding by viewBinding(FragmentListBinding::bind)

    private lateinit var adapter : TrainPagingAdapter

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

    private var mTrainList: PagingData<TrainMinimal>? = null

    private var addMenuItem: MenuItem? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(TrainViewModel::class.java)

        adapter = TrainPagingAdapter(requireContext(), this, this)

        binding.list.adapter = adapter

        ItemTouchHelper(SwipeToDeleteCallback(requireContext(), adapter)).attachToRecyclerView(
            binding.list
        )

        observeUIState(R.string.no_trains_found)

        lifecycleScope.launch {
            viewModel.allItems.collectLatest { trainEntries ->
                adapter.submitData(trainEntries)
                mTrainList = trainEntries
            }
        }
    }

    private fun observeUIState(@StringRes message: Int) {
        Timber.d("observeUIState is called")
        binding.showLoading()

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest {
                Timber.d("New LoadState is received")
                when (it.refresh) {
                    is LoadState.Loading -> {
                        binding.showLoading()
                        Timber.d("state is Loading")
                    }
                    is LoadState.NotLoading -> {
                        Timber.d("state is Not-Loading")
                        if (adapter.itemCount < 1) {
                            binding.showEmpty(message)
                            Timber.d("list is empty")
                        } else {
                            binding.showList()
                            Timber.d("list is not empty")
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_item, menu)
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
            R.id.trashFragment -> NavigationUI.onNavDestinationSelected(
                item, binding.root.findNavController()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDeleteConfirmed(itemToDelete: TrainMinimal, position: Int) {
        viewModel.sendTrainToTrash(itemToDelete.trainId)
        adapter.itemDeleted(position)
    }

    override fun onDeleteCanceled(position: Int) = adapter.cancelDelete(position)

    fun scrollToTop() {
        binding.list.smoothScrollToPosition(0)
    }

    override fun onListItemClick(view: View, trainId: Int) {
        val action = TrainListFragmentDirections.actionTrainListFragmentToTrainDetailsFragment(trainId)
        binding.root.findNavController().navigate(action)
    }
}
