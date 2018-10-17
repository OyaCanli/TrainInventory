package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.databinding.FragmentTrainDetailsBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.ChosenTrainFactory;
import com.canli.oya.traininventory.viewmodel.ChosenTrainViewModel;

public class TrainDetailsFragment extends Fragment {

    private FragmentTrainDetailsBinding binding;
    private TrainDatabase mDb;
    private TrainEntry mChosenTrain;
    private int mTrainId;

    public TrainDetailsFragment() { }

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
        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTrainId = bundle.getInt(Constants.TRAIN_ID);
        }
        ChosenTrainFactory factory = InjectorUtils.provideChosenTrainFactory(getActivity(), mTrainId);
        final ChosenTrainViewModel viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel.class);
        viewModel.getChosenTrain().observe(this, new Observer<TrainEntry>() {
            @Override
            public void onChanged(@Nullable TrainEntry trainEntry) {
                populateUI(trainEntry);
                mChosenTrain = trainEntry;
            }
        });
    }

    private void populateUI(TrainEntry chosenTrain) {
        binding.setChosenTrain(chosenTrain);
        binding.executePendingBindings();
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
                addTrainFrag.setEnterTransition(new Slide(Gravity.END));
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addTrainFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setMessage("Do you want to delete this item from the database?");
        builder.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTrain();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create();
        builder.show();
    }

    private void deleteTrain() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.trainDao().deleteTrain(mChosenTrain);
                getFragmentManager().popBackStack();
            }
        });
    }
}
