package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.TrainAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.TrainMinimal;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.TrainListViewModel;

import java.util.List;

public class TrainListFragment extends Fragment implements TrainAdapter.ListItemClickListener{


    private TextView empty_tv;
    private RecyclerView recycler;
    private TrainAdapter mAdapter;
    private ConstraintLayout empty_screen;
    private TrainDatabase mDb;

    public TrainListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        TrainListViewModel viewModel = ViewModelProviders.of(this).get(TrainListViewModel.class);
        viewModel.getTrains().observe(this, new Observer<List<TrainMinimal>>() {
            @Override
            public void onChanged(@Nullable List<TrainMinimal> trainMinimals) {
                mAdapter.setTrains(trainMinimals);
            }
        });

        getActivity().setTitle(getString(R.string.all_trains));

        mAdapter = new TrainAdapter(getActivity(), this);
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(mAdapter);
        empty_screen = rootView.findViewById(R.id.empty_view);
        empty_tv = rootView.findViewById(R.id.empty_text);

        return rootView;
    }

    @Override
    public void onListItemClick(int trainId) {
        TrainDetailsFragment trainDetailsFrag = new TrainDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.TRAIN_ID, trainId);
        trainDetailsFrag.setArguments(args);
        trainDetailsFrag.setEnterTransition(new Slide(Gravity.END));
        trainDetailsFrag.setExitTransition(new Slide(Gravity.START));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, trainDetailsFrag)
                .addToBackStack(null)
                .commit();
    }
}
