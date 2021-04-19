package com.canli.oya.traininventoryroom.ui.trash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentTrashBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.trains.TrainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class TrashFragment : Fragment(R.layout.fragment_trash), TrashItemClickListener {

    private val binding by viewBinding(FragmentTrashBinding::bind)

    private lateinit var viewModel: TrainViewModel

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

    private lateinit var trashAdapter: TrashAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        trashAdapter = TrashAdapter(this)
        binding.trashList.adapter = trashAdapter

        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(TrainViewModel::class.java)

        lifecycleScope.launch {
            viewModel.getTrainsInTrash().observe(viewLifecycleOwner) { trashList ->
                if (trashList.isEmpty()) {
                    setUIEmpty(R.string.no_trains_in_trash)
                    Timber.d("Trash folder list is empty")
                } else {
                    trashAdapter.submitList(trashList)
                    Timber.d("Number of trains in trash: ${trashList.size}")
                    setUISuccess()
                }
            }
        }
    }

    protected fun setUIEmpty(emptyMessage: Int) {
        binding.trashList.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        binding.emptyImage.visibility = View.VISIBLE
        binding.emptyText.setText(emptyMessage)
        binding.emptyText.visibility = View.VISIBLE
    }

    protected fun setUILoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.emptyImage.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
        binding.trashList.visibility = View.GONE
    }

    protected fun setUISuccess() {
        binding.trashList.visibility = View.VISIBLE
        binding.loadingIndicator.visibility = View.GONE
        binding.emptyImage.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
    }

    override fun onRestoreClicked(trainId: Int) {
        lifecycleScope.launch {
            viewModel.restoreTrain(trainId)
        }
    }

    override fun onDeleteClicked(trainId: Int) {
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