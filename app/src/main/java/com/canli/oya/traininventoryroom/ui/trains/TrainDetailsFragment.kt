package com.canli.oya.traininventoryroom.ui.trains

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentTrainDetailsBinding
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.main.Navigator
import com.canli.oya.traininventoryroom.utils.TRAIN_ID
import javax.inject.Inject

class TrainDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTrainDetailsBinding
    private lateinit var mChosenTrain: TrainEntry
    private lateinit var viewModel : TrainViewModel

    @Inject
    lateinit var navigator : Navigator

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory
    private var trainId = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_train_details, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        trainId = arguments?.getInt(TRAIN_ID) ?: 0

        (activity?.application as TrainApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainViewModel::class.java)

        viewModel.getChosenTrain(trainId).observe(viewLifecycleOwner, Observer { trainEntry ->
            trainEntry?.let {
                binding.chosenTrain = it
                mChosenTrain = it
                activity?.title = it.trainName
            } })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_train_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> openAlertDialogForDelete()
            R.id.action_edit -> navigator.launchEditTrain(mChosenTrain)
            android.R.id.home -> activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAlertDialogForDelete() {
        val builder = AlertDialog.Builder(activity!!, R.style.alert_dialog_style)
        with(builder){
            setMessage(R.string.do_you_want_to_delete)
            setPositiveButton(R.string.yes_delete) { _, _ -> deleteTrain() }
            setNegativeButton(R.string.cancel) {_, _ -> }
            create()
            show()
        }
    }

    private fun deleteTrain() {
        viewModel.deleteTrain(mChosenTrain)
        fragmentManager?.popBackStack()
    }

}
