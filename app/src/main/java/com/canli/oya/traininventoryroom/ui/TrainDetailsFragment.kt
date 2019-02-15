package com.canli.oya.traininventoryroom.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.databinding.FragmentTrainDetailsBinding
import com.canli.oya.traininventoryroom.utils.InjectorUtils
import com.canli.oya.traininventoryroom.utils.TRAIN_ID
import com.canli.oya.traininventoryroom.viewmodel.ChosenTrainViewModel
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel

class TrainDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTrainDetailsBinding
    private var mChosenTrain: TrainEntry? = null
    private var mTrainId: Int = 0
    private val mainViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_train_details, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mTrainId = arguments?.getInt(TRAIN_ID) ?: 0

        val factory = InjectorUtils.provideChosenTrainFactory(requireContext(), mTrainId)
        val viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel::class.java)
        viewModel.chosenTrain.observe(this, Observer { trainEntry ->
            trainEntry?.let {
                populateUI(it)
                mChosenTrain = it
            }
        })
    }

    private fun populateUI(chosenTrain: TrainEntry) {
        activity!!.title = chosenTrain.trainName
        binding.chosenTrain = chosenTrain
        binding.executePendingBindings()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_train_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_delete -> {
                openAlertDialogForDelete()
            }
            R.id.action_edit -> {
                val addTrainFrag = AddTrainFragment()
                val args = Bundle()
                args.putInt(TRAIN_ID, mTrainId)
                addTrainFrag.arguments = args
                val fm = fragmentManager
                fm!!.beginTransaction()
                        .replace(R.id.container, addTrainFrag)
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .addToBackStack(null)
                        .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAlertDialogForDelete() {
        val builder = AlertDialog.Builder(activity!!, R.style.Theme_AppCompat_DayNight_Dialog)
        builder.setMessage(R.string.do_you_want_to_delete)
        builder.setPositiveButton(R.string.yes_delete) { dialog, which -> deleteTrain() }
        builder.setNegativeButton(R.string.cancel) { dialog, id -> }
        builder.create()
        builder.show()
    }

    private fun deleteTrain() {
        mainViewModel.deleteTrain(mChosenTrain!!)
        fragmentManager?.popBackStack()
    }

}
