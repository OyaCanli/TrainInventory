package com.canli.oya.traininventory.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.databinding.FragmentAddCategoryBinding;


public class AddCategoryFragment extends Fragment {

    private FragmentAddCategoryBinding binding;

    public AddCategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_category, container, false);

        binding.addCategoryEditCatName.requestFocus();
        binding.addCategorySaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

        return binding.getRoot();
    }

    private void saveCategory() {
        final TrainDatabase database = TrainDatabase.getInstance(getActivity().getApplicationContext());
        String categoryName = binding.addCategoryEditCatName.getText().toString().trim();
        final CategoryEntry newCategory = new CategoryEntry(categoryName);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.categoryDao().insertCategory(newCategory);
            }
        });

        //Remove the fragment
        Fragment parentFrag = getParentFragment();
        Fragment currentInstance;
        if (parentFrag instanceof AddTrainFragment) {
            currentInstance = getFragmentManager().findFragmentById(R.id.childFragContainer);
        } else {
            currentInstance = getFragmentManager().findFragmentById(R.id.brandlist_addFrag_container);
        }

        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(currentInstance)
                .commit();
    }
}
