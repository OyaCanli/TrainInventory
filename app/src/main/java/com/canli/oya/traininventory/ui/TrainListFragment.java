package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.TrainAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.databinding.FragmentListBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.SearchViewModel;
import com.canli.oya.traininventory.viewmodel.SearchViewModelFactory;
import com.canli.oya.traininventory.viewmodel.TrainsViewModel;
import com.canli.oya.traininventory.viewmodel.TrainsViewModelFactory;

import java.util.List;

public class TrainListFragment extends Fragment implements TrainAdapter.TrainItemClickListener {

    private TrainAdapter mAdapter;
    private List<TrainEntry> mTrainList;
    private TrainDatabase mDb;
    private List<TrainEntry> filteredTrains;
    private FragmentListBinding binding;

    public TrainListFragment() {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_list, container, false);
        setHasOptionsMenu(true);

        //Set recycler view
        mAdapter = new TrainAdapter(getActivity(), this);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.list.setItemAnimator(new DefaultItemAnimator());
        binding.list.setAdapter(mAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getString(R.string.all_trains));

        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        Bundle bundle = getArguments();
        //If the list will be used for showing selected trains
        if(bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)){
            SearchViewModelFactory searchFactory = InjectorUtils.provideSearchVMFactory(getActivity());
            SearchViewModel searchVM = ViewModelProviders.of(this, searchFactory).get(SearchViewModel.class);
            if(bundle.getString(Constants.INTENT_REQUEST_CODE).equals(Constants.TRAINS_OF_BRAND)){
                searchVM.getTrainsFromThisBrand(bundle.getString(Constants.BRAND_NAME)).observe(TrainListFragment.this, new Observer<List<TrainEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                        if(trainEntries == null || trainEntries.isEmpty()){
                            binding.setIsEmpty(true);
                            binding.setEmptyMessage(getString(R.string.no_train_for_this_brand));
                        } else {
                            binding.setIsEmpty(false);
                            mAdapter.setTrains(trainEntries);
                            mTrainList = trainEntries;
                        }
                    }
                });
            } else {
                searchVM.getTrainsFromThisCategory(bundle.getString(Constants.CATEGORY_NAME)).observe(TrainListFragment.this, new Observer<List<TrainEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                        if(trainEntries == null || trainEntries.isEmpty()){
                            binding.setIsEmpty(true);
                            binding.setEmptyMessage(getString(R.string.no_items_for_this_category));
                        } else{
                            binding.setIsEmpty(false);
                            mAdapter.setTrains(trainEntries);
                            mTrainList = trainEntries;
                        }
                    }
                });
            }
        } else {
            //If the list is going to be use for showing all trains, which is the default behaviour
            TrainsViewModelFactory factory = InjectorUtils.provideTrainVMFactory(getActivity());
            TrainsViewModel viewModel = ViewModelProviders.of(TrainListFragment.this, factory).get(TrainsViewModel.class);
            viewModel.getTrainList().observe(TrainListFragment.this, new Observer<List<TrainEntry>>() {

                @Override
                public void onChanged(@Nullable List<TrainEntry> trainEntries) {
                    if(trainEntries == null || trainEntries.isEmpty()){
                        binding.setIsEmpty(true);
                        binding.setEmptyMessage(getString(R.string.no_trains_found));
                    } else{
                        binding.setIsEmpty(false);
                        mAdapter.setTrains(trainEntries);
                        mTrainList = trainEntries;
                    }
                }
            });
        }
    }

    @Override
    public void onListItemClick(int trainId) {
        TrainDetailsFragment trainDetailsFrag = new TrainDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.TRAIN_ID, trainId);
        trainDetailsFrag.setArguments(args);
        loadFragment(trainDetailsFrag, Constants.TAG_DETAILS);
    }

    private void loadFragment(Fragment newFrag, String tag) {
        //This method loads a new fragment, if there isn't already an instance of it.
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);
        if(fragment == null) {
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
                filterTrains(query);
                return false;
            }
        });
    }

    private void filterTrains(final String query){
        if (query == null || "".equals(query)) {
            filteredTrains = mTrainList;
            mAdapter.setTrains(filteredTrains);
            mAdapter.notifyDataSetChanged();
        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    filteredTrains = mDb.trainDao().searchInTrains(query);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setTrains(filteredTrains);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            openAddTrainFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddTrainFragment(){
        loadFragment(new AddTrainFragment(), Constants.TAG_ADD_TRAIN);
    }

    public void scrollToTop(){
        binding.list.smoothScrollToPosition(0);
    }
}
