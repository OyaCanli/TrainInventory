package com.canli.oya.traininventoryroom.ui.trash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.BaseTrainAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener
import com.canli.oya.traininventoryroom.ui.trains.TrainViewModel
import com.canli.oya.traininventoryroom.utils.showEmpty
import com.canli.oya.traininventoryroom.utils.showList
import com.canli.oya.traininventoryroom.utils.showLoading
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class TrashListFragment : Fragment(R.layout.fragment_list), TrainItemClickListener {

    private val binding by viewBinding(FragmentListBinding::bind)

    private lateinit var viewModel: TrainViewModel

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

    private lateinit var trashAdapter: BaseTrainAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        trashAdapter = TrashTrainAdapter(this)
        binding.list.adapter = trashAdapter

        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(TrainViewModel::class.java)

        lifecycleScope.launch {
            binding.showLoading()
            viewModel.getTrainsInTrash().observe(viewLifecycleOwner) { trashList ->
                if (trashList.isEmpty()) {
                    binding.showEmpty(R.string.no_trains_in_trash)
                    Timber.d("Trash folder list is empty")
                } else {
                    trashAdapter.submitList(trashList)
                    Timber.d("Number of trains in trash: ${trashList.size}")
                    binding.showList()
                }
            }
        }
    }

    override fun onListItemClick(view: View, trainId: Int) {
        when(view.id) {
            R.id.train_item_delete -> onDeleteClicked(trainId)
            R.id.train_item_restore -> onRestoreClicked(trainId)
        }
    }

    private fun onRestoreClicked(trainId: Int) {
        lifecycleScope.launch {
            viewModel.restoreTrain(trainId)
        }
    }

    private fun onDeleteClicked(trainId: Int) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.alert_dialog_style)
        with(builder) {
            setMessage(getString(R.string.do_you_want_to_permanently_delete_train))
            setPositiveButton(R.string.yes_delete) { _, _ ->
                GlobalScope.launch {
                    viewModel.deleteTrainPermanently(trainId)
                }
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
            create()
            show()
        }
    }
}