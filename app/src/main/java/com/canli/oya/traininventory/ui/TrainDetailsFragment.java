package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.TrainEntry;
import com.canli.oya.traininventory.databinding.FragmentTrainDetailsBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.ChosenTrainViewModel;
import com.canli.oya.traininventory.utils.ChosenTrainViewModelFactory;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.GlideApp;

public class TrainDetailsFragment extends Fragment {

    private FragmentTrainDetailsBinding binding;
    TrainDatabase mDb;
    int mTrainId;
    TrainEntry mChosenTrain;

    public TrainDetailsFragment(){
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_train_details, container, false);
        setHasOptionsMenu(true);
        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        Bundle bundle = getArguments();
        mTrainId = bundle.getInt(Constants.TRAIN_ID);

        ChosenTrainViewModelFactory factory = new ChosenTrainViewModelFactory(mDb, mTrainId);
        final ChosenTrainViewModel viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel.class);
        viewModel.getChosenTrain().observe(this, new Observer<TrainEntry>() {
            @Override
            public void onChanged(@Nullable TrainEntry trainEntry) {
                populateUI(trainEntry);
                mChosenTrain = trainEntry;
            }
        });
        return binding.getRoot();
    }

    private void populateUI(TrainEntry chosenTrain) {
        binding.detailsBrand.setText(chosenTrain.getBrandName());
        binding.detailsProductName.setText(String.valueOf(chosenTrain.getTrainId()));
        binding.detailsReference.setText(chosenTrain.getModelReference());
        binding.detailsCategory.setText(chosenTrain.getCategoryName());
        binding.detailsQuantity.setText(String.valueOf(chosenTrain.getQuantity()));
        binding.detailsLocation.setText(chosenTrain.getLocation());
        GlideApp.with(TrainDetailsFragment.this)
                .load(chosenTrain.getImageUri())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.detailsImage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_train_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_delete:{
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.trainDao().deleteTrain(mChosenTrain);
                        TrainListFragment trainListFrag = new TrainListFragment();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, trainListFrag)
                                .commit();
                    }
                });
                break;
            }
            case R.id.action_edit:{
                AddTrainFragment addTrainFrag = new AddTrainFragment();
                Bundle args = new Bundle();
                args.putInt(Constants.TRAIN_ID, mTrainId);
                addTrainFrag.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addTrainFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
