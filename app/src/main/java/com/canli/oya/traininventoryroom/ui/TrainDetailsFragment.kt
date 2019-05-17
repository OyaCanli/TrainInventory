package com.canli.oya.traininventoryroom.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentTrainDetailsBinding
import com.canli.oya.traininventoryroom.utils.CHOSEN_TRAIN
import com.canli.oya.traininventoryroom.utils.TRAIN_ID
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel

class TrainDetailsFragment : androidx.fragment.app.Fragment() {

    private lateinit var binding: FragmentTrainDetailsBinding
    private lateinit var mChosenTrain: TrainEntry
    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }
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
        val chosenTrainLiveData = mViewModel.getChosenTrain(trainId)
        binding.chosenTrain = chosenTrainLiveData
        binding.lifecycleOwner = viewLifecycleOwner
        chosenTrainLiveData.observe(viewLifecycleOwner, Observer { trainEntry ->
            trainEntry?.let {
                mChosenTrain = it
                activity?.title = it.trainName
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_train_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                openAlertDialogForDelete()
            }
            R.id.action_edit -> {
                val addTrainFrag = AddTrainFragment()
                val args = Bundle()
                args.putParcelable(CHOSEN_TRAIN, mChosenTrain)
                addTrainFrag.arguments = args
                fragmentManager?.transaction { replace(R.id.container, addTrainFrag)
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .addToBackStack(null)}
            }
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
        mViewModel.deleteTrain(mChosenTrain)
        fragmentManager?.popBackStack()
    }

}
