package com.canli.oya.traininventory.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.utils.AppExecutors;

public class AddCategoryFragment extends Fragment {

    EditText categoryName_et;

    public AddCategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        categoryName_et = rootView.findViewById(R.id.addCategory_editCatName);
        categoryName_et.requestFocus();
        Button save_category_btn = rootView.findViewById(R.id.addCategory_saveBtn);
        save_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });
        return rootView;
    }

    private void saveCategory(){
        final TrainDatabase database = TrainDatabase.getInstance(getActivity().getApplicationContext());
        String categoryName = categoryName_et.getText().toString().trim();
        final CategoryEntry newCategory = new CategoryEntry(categoryName);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.categoryDao().insertCategory(newCategory);
            }
        });
        Fragment parentFrag = getParentFragment();
        if(parentFrag != null){
            //Remove the fragment
            Fragment frag = getFragmentManager().findFragmentById(R.id.childFragContainer);
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(frag)
                    .commit();
        }
    }
}
