package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.TrainAdapter;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class TrainListFragment extends Fragment implements TrainAdapter.ListItemClickListener{

    private RecyclerView recycler;
    private TrainAdapter mAdapter;
    private MainViewModel viewModel;
    private List<TrainEntry> mTrainList;
    private TextView empty_tv;
    private ImageView empty_image;

    public TrainListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        getActivity().setTitle(getString(R.string.all_trains));

        mAdapter = new TrainAdapter(getActivity(), this);
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getTrains().observe(getActivity(), new Observer<List<TrainEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                       if(trainEntries.isEmpty()){
                           showEmpty();
                       } else{
                           mAdapter.setTrains(trainEntries);
                           mTrainList = trainEntries;
                       }
                    }
                });


        empty_tv = rootView.findViewById(R.id.empty_text);
        empty_image = rootView.findViewById(R.id.empty_image);

        return rootView;
    }

    private void showEmpty(){
        recycler.setVisibility(View.GONE);
        empty_tv.setText(R.string.no_trains_found);
        empty_tv.setVisibility(View.VISIBLE);
        empty_image.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(int position) {
        viewModel.setChosenTrain(mTrainList.get(position));
        TrainDetailsFragment trainDetailsFrag = new TrainDetailsFragment();
        trainDetailsFrag.setEnterTransition(new Slide(Gravity.END));
        trainDetailsFrag.setExitTransition(new Slide(Gravity.START));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, trainDetailsFrag)
                .addToBackStack(null)
                .commit();
    }
}
