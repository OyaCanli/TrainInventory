package com.canli.oya.traininventory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.CategoryAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.util.List;

public class CategoryListFragment extends Fragment {

    private TrainDatabase mDb;
    private TextView empty_tv;
    private ImageView empty_image;
    private RecyclerView recycler;
    private CategoryAdapter mAdapter;
    private List<CategoryEntry> mCategories;

    public CategoryListFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brandlist, container, false);
        setHasOptionsMenu(true);

        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        mAdapter = new CategoryAdapter();
        recycler = rootView.findViewById(R.id.list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(mAdapter);

        empty_tv = rootView.findViewById(R.id.empty_text);
        empty_image = rootView.findViewById(R.id.empty_image);

        final MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getCategoryList().observe(getActivity(), new Observer<List<CategoryEntry>>() {
            @Override
            public void onChanged(@Nullable List<CategoryEntry> categoryEntries) {
                if (categoryEntries.isEmpty()) {
                    showEmpty();
                } else {
                    mAdapter.setCategories(categoryEntries);
                    mCategories = categoryEntries;
                    showData();
                }
            }
        });

        getActivity().setTitle(getString(R.string.all_categories));

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
                final CategoryEntry categoryToErase = mCategories.get(position);

                //Remove the category from the database
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.categoryDao().deleteCategory(categoryToErase);
                    }
                });

                //Show a snackbar for undoing delete
                Snackbar snackbar = Snackbar
                        .make(coordinator, R.string.brand_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //If UNDO is clicked, add the item back in the database
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDb.categoryDao().insertCategory(categoryToErase);
                                    }
                                });
                            }
                        });
                snackbar.show();
            }
        }).attachToRecyclerView(recycler);

        return rootView;
    }

    private void showEmpty() {
        recycler.setVisibility(View.GONE);
        empty_tv.setText(R.string.no_categories_found);
        empty_tv.setVisibility(View.VISIBLE);
        empty_image.setVisibility(View.VISIBLE);
    }

    private void showData() {
        if (empty_image.getVisibility() == View.VISIBLE) {
            empty_tv.setVisibility(View.GONE);
            empty_image.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            openAddCategoryFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddCategoryFragment(){
        AddCategoryFragment addCatFrag = new AddCategoryFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.brandlist_addFrag_container, addCatFrag)
                .commit();
    }

}
