package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.CategoryAdapter;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.databinding.FragmentBrandlistBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class CategoryListFragment extends Fragment implements CategoryAdapter.CategoryItemClickListener {

    private CategoryAdapter mAdapter;
    private List<String> mCategories;
    private FragmentBrandlistBinding binding;
    private MainViewModel mViewModel;
    private FragmentSwitchListener mCallback;

    public CategoryListFragment() {
        setRetainInstance(true);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (FragmentSwitchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentSwitchListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_brandlist, container, false);

        setHasOptionsMenu(true);

        mAdapter = new CategoryAdapter(this);

        binding.included.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.included.list.setItemAnimator(new DefaultItemAnimator());
        binding.included.list.setAdapter(mAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mViewModel.loadCategoryList(InjectorUtils.provideCategoryRepo(getContext()));
        mViewModel.getCategoryList().observe(CategoryListFragment.this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> categoryEntries) {
                if (categoryEntries == null || categoryEntries.isEmpty()) {
                    binding.included.setIsEmpty(true);
                    binding.included.setEmptyMessage(getString(R.string.no_categories_found));
                } else {
                    mAdapter.setCategories(categoryEntries);
                    mCategories = categoryEntries;
                    binding.included.setIsEmpty(false);
                }
            }
        });

        getActivity().setTitle(getString(R.string.all_categories));

        //This part is for providing swipe-to-delete functionality, as well as a snack bar to undo deleting
        final CoordinatorLayout coordinator = getActivity().findViewById(R.id.coordinator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                //First take a backup of the category to erase
                final CategoryEntry categoryToErase = new CategoryEntry(mCategories.get(position));

                //Remove the category from the database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //First check whether this category is used by trains table
                        if (mViewModel.isThisCategoryUsed(categoryToErase.getCategoryName())) {
                            // If it is used, show a warning and don't let user delete this
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.cannot_erase_category, Toast.LENGTH_LONG).show();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            //If it is not used, erase the category
                            mViewModel.deleteCategory(categoryToErase);
                            //Show a snack bar for undoing delete
                            Snackbar snackbar = Snackbar
                                    .make(coordinator, R.string.category_deleted, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //If UNDO is clicked, add the item back in the database
                                            mViewModel.insertCategory(categoryToErase);
                                        }
                                    });
                            snackbar.show();
                        }
                    }
                });
            }
        }).attachToRecyclerView(binding.included.list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            openAddCategoryFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddCategoryFragment() {
        AddCategoryFragment addCatFrag = new AddCategoryFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addCatFrag)
                .commit();
    }

    @Override
    public void onCategoryItemClicked(String categoryName) {
        TrainListFragment trainListFrag = new TrainListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_REQUEST_CODE, Constants.TRAINS_OF_CATEGORY);
        args.putString(Constants.CATEGORY_NAME, categoryName);
        trainListFrag.setArguments(args);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, trainListFrag)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .commit();
        fm.executePendingTransactions();
        mCallback.onFragmentSwitched();
    }
}
