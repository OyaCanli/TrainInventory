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
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.TrainAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class TrainListFragment extends Fragment implements TrainAdapter.TrainItemClickListener {

    private RecyclerView recycler;
    private TrainAdapter mAdapter;
    private MainViewModel viewModel;
    private List<TrainEntry> mTrainList;
    private TextView empty_tv;
    private ImageView empty_image;

    public TrainListFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        getActivity().setTitle(getString(R.string.all_trains));
        setHasOptionsMenu(true);

        mAdapter = new TrainAdapter(getActivity(), this);
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(mAdapter);

        empty_tv = rootView.findViewById(R.id.empty_text);
        empty_image = rootView.findViewById(R.id.empty_image);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)){
            TrainDatabase mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());
            if(bundle.getString(Constants.INTENT_REQUEST_CODE) == Constants.TRAINS_OF_BRAND){
                mDb.trainDao().getTrainsFromThisBrand(bundle.getString(Constants.BRAND_NAME)).observe(TrainListFragment.this, new Observer<List<TrainEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                        if(trainEntries.isEmpty()){
                            showEmpty(getString(R.string.no_train_for_this_brand));
                        } else{
                            mAdapter.setTrains(trainEntries);
                            mTrainList = trainEntries;
                        }
                    }
                });
            }else{
                mDb.trainDao().getTrainsFromThisCategory(bundle.getString(Constants.CATEGORY_NAME)).observe(TrainListFragment.this, new Observer<List<TrainEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                        if(trainEntries.isEmpty()){
                            showEmpty(getString(R.string.no_items_for_this_category));
                        } else{
                            mAdapter.setTrains(trainEntries);
                            mTrainList = trainEntries;
                        }
                    }
                });
            }
        }else{
            viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
            viewModel.getAllTrains().observe(getActivity(), new Observer<List<TrainEntry>>() {
                @Override
                public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                    if(trainEntries.isEmpty()){
                        showEmpty(getString(R.string.no_trains_found));
                    } else{
                        mAdapter.setTrains(trainEntries);
                        mTrainList = trainEntries;
                    }
                }
            });
        }

        return rootView;
    }

    private void showEmpty(String message){
        recycler.setVisibility(View.GONE);
        empty_tv.setText(message);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_and_add, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //added filter to list (dynamic change input text)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //TODO: implement filtering here
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            openAddTrainFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddTrainFragment(){
        AddTrainFragment addTrainFrag = new AddTrainFragment();
        addTrainFrag.setEnterTransition(new Slide(Gravity.END));
        addTrainFrag.setExitTransition(new Slide(Gravity.START));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, addTrainFrag)
                .addToBackStack(null)
                .commit();
    }
}
