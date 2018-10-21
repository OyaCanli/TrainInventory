package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.data.repositories.TrainRepository;
import com.canli.oya.traininventory.databinding.FragmentTrainDetailsBinding;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.ChosenTrainFactory;
import com.canli.oya.traininventory.viewmodel.ChosenTrainViewModel;

public class TrainDetailsFragment extends Fragment {

    private FragmentTrainDetailsBinding binding;
    private TrainEntry mChosenTrain;
    private int mTrainId;

    public TrainDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_train_details, container, false);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTrainId = bundle.getInt(Constants.TRAIN_ID);
        }
        ChosenTrainFactory factory = InjectorUtils.provideChosenTrainFactory(getActivity(), mTrainId);
        ChosenTrainViewModel viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel.class);
        viewModel.getChosenTrain().observe(this, new Observer<TrainEntry>() {
            @Override
            public void onChanged(@Nullable TrainEntry trainEntry) {
                if (trainEntry != null) {
                    populateUI(trainEntry);
                    mChosenTrain = trainEntry;
                }
            }
        });
    }

    private void populateUI(TrainEntry chosenTrain) {
        getActivity().setTitle(chosenTrain.getTrainName());
        binding.setChosenTrain(chosenTrain);
        binding.executePendingBindings();
    }

    private void loadFragment(Fragment newFrag) {
        //This method loads a new fragment, if there isn't already an instance of it.
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String tag = Constants.TAG_ADD_TRAIN;
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newFrag;
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.container, fragment, tag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_train_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                openAlertDialogForDelete();
                break;
            }
            case R.id.action_edit: {
                AddTrainFragment addTrainFrag = new AddTrainFragment();
                Bundle args = new Bundle();
                args.putInt(Constants.TRAIN_ID, mTrainId);
                addTrainFrag.setArguments(args);
                loadFragment(addTrainFrag);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setMessage(R.string.do_you_want_to_delete);
        builder.setPositiveButton(R.string.yes_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTrain();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create();
        builder.show();
    }

    private void deleteTrain() {
        TrainRepository trainRepo = InjectorUtils.provideTrainRepo(getContext());
        trainRepo.deleteTrain(mChosenTrain);
        getFragmentManager().popBackStack();
    }
}
