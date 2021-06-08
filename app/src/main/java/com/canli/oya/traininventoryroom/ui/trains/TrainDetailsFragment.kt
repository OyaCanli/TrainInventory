package com.canli.oya.traininventoryroom.ui.trains

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentTrainDetailsBinding
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.main.MainActivity
import com.canlioya.core.models.Train
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrainDetailsFragment : Fragment(R.layout.fragment_train_details) {

    private val binding by viewBinding(FragmentTrainDetailsBinding::bind)

    private lateinit var mChosenTrain: Train

    private val viewModel: TrainViewModel by activityViewModels()

    private var trainId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainId = TrainDetailsFragmentArgs.fromBundle(requireArguments()).trainId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel.getChosenTrain(trainId).observe(viewLifecycleOwner) { trainEntry ->
            binding.chosenTrain = trainEntry
            mChosenTrain = trainEntry
            (activity as? MainActivity)?.supportActionBar?.title = trainEntry.trainName
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_train_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> openAlertDialogForDelete()
            R.id.action_edit -> launchEditTrain()
            android.R.id.home -> activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchEditTrain() {
        val action =
            TrainDetailsFragmentDirections.actionTrainDetailsFragmentToAddTrainFragment(mChosenTrain)
        binding.root.findNavController().navigate(action)
    }

    private fun openAlertDialogForDelete() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.alert_dialog_style)
        with(builder) {
            setMessage(R.string.do_you_want_to_delete)
            setPositiveButton(R.string.yes_delete) { _, _ -> deleteTrain() }
            setNegativeButton(R.string.cancel) { _, _ -> }
            create()
            show()
        }
    }

    private fun deleteTrain() {
        viewModel.sendTrainToTrash(mChosenTrain.trainId)
        binding.root.findNavController().popBackStack()
    }

}
