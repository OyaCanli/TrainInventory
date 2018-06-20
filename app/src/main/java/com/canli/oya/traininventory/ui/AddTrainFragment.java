package com.canli.oya.traininventory.ui;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.TrainEntry;
import com.canli.oya.traininventory.databinding.FragmentAddTrainBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.BitmapUtils;
import com.canli.oya.traininventory.utils.CategoryBrandViewModel;
import com.canli.oya.traininventory.utils.ChosenTrainViewModel;
import com.canli.oya.traininventory.utils.ChosenTrainViewModelFactory;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.GlideApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddTrainFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener{

    private FragmentAddTrainBinding binding;
    private String mChosenCategory;
    private String mChosenBrand;
    private TrainDatabase mDb;
    private AlertDialog pickImageDialog;
    private String mTempPhotoPath;
    private Uri mImageUri;
    private int mUsersChoice;
    private List<String> categoryList;
    private List<String> brandList;

    private final DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
            mUsersChoice = item;
        }
    };

    public AddTrainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_train, container, false);
        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(Constants.TRAIN_ID)){ //This is the "edit" case
            int mTrainId = bundle.getInt(Constants.TRAIN_ID);
            //This view model is instantiated only in edit mode.
            ChosenTrainViewModelFactory factory = new ChosenTrainViewModelFactory(mDb, mTrainId);
            final ChosenTrainViewModel viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel.class);
            viewModel.getChosenTrain().observe(this, new Observer<TrainEntry>() {
                @Override
                public void onChanged(@Nullable TrainEntry trainEntry) {
                    populateFields(trainEntry);
                }
            });
        }

        CategoryBrandViewModel cbViewModel= ViewModelProviders.of(this).get(CategoryBrandViewModel.class);

        //Set category spinner
        categoryList = new ArrayList<>();
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
        binding.categorySpinner.setOnItemSelectedListener(this);
        cbViewModel.getCategoryList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                categoryList.clear();
                categoryList.addAll(strings);
                categoryAdapter.notifyDataSetChanged();
            }
        });

        //Set brand spinner
        brandList = new ArrayList<>();
        final ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, brandList);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.brandSpinner.setAdapter(brandAdapter);
        binding.brandSpinner.setOnItemSelectedListener(this);
        cbViewModel.getBrandList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                brandList.clear();
                brandList.addAll(strings);
                brandAdapter.notifyDataSetChanged();
            }
        });

        //Set click listener on buttons
        binding.addTrainAddBrandBtn.setOnClickListener(this);
        binding.addTrainAddCategoryBtn.setOnClickListener(this);
        binding.saveBtn.setOnClickListener(this);
        binding.productDetailsGalleryImage.setOnClickListener(this);

        return binding.getRoot();
    }

    private void populateFields(TrainEntry trainToEdit){
        binding.brandSpinner.setSelection(categoryList.indexOf(trainToEdit.getCategoryName()));
        binding.brandSpinner.setSelection(brandList.indexOf(trainToEdit.getBrandName()));
        binding.editReference.setText(trainToEdit.getModelReference());
        binding.editTrainName.setText(trainToEdit.getTrainName());
        binding.editQuantity.setText(String.valueOf(trainToEdit.getQuantity()));
        binding.editTrainDescription.setText(trainToEdit.getDescription());
        GlideApp.with(AddTrainFragment.this)
                .load(trainToEdit.getImageUri())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.productDetailsGalleryImage);
        String[] locationParts = trainToEdit.getLocation().split("-");
        binding.editLocationNumber.setText(locationParts[0]);
        binding.editLocationLetter.setText(locationParts[1]);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save_btn:{
                saveTrain();
                break;
            }
            case R.id.addTrain_addBrandBtn:{
                AddBrandFragment addBrandFrag = new AddBrandFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.childFragContainer, addBrandFrag)
                        .commit();
                break;
            }
            case R.id.addTrain_addCategoryBtn:{
                AddCategoryFragment addCatFrag = new AddCategoryFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.childFragContainer, addCatFrag)
                        .commit();
                break;
            }
            case R.id.product_details_gallery_image:{
                openImageDialog();
                break;
            }
        }
    }

    private void saveTrain(){
        String quantityToParse = binding.editQuantity.getText().toString().trim();
        //Quantity can be null. But if it is not null it should be a positive integer
        int quantity = 0;
        if(!TextUtils.isEmpty(quantityToParse)){
            try {
                quantity = Integer.valueOf(quantityToParse);
                if (quantity < 0) {
                    Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException nfe) {
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String reference = binding.editReference.getText().toString().trim();
        String trainName = binding.editTrainName.getText().toString().trim();
        String description = binding.editTrainDescription.getText().toString().trim();
        String location = binding.editLocationNumber.getText().toString().trim() + "-" +
                binding.editLocationLetter.getText().toString().trim();

        final TrainEntry newTrain = new TrainEntry(trainName, reference, mChosenBrand, mChosenCategory, quantity, mImageUri.toString(), description, location);
        Log.v("AddTrainFragment", "new train:" + newTrain.toString());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.trainDao().insertTrain(newTrain);
            }
        });
        TrainListFragment trainListFrag = new TrainListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, trainListFrag)
                .addToBackStack(null)
                .commit();
    }

    private void openImageDialog() {
        String[] dialogOptions = getActivity().getResources().getStringArray(R.array.dialog_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add image from: ");
        builder.setSingleChoiceItems(dialogOptions, -1, mDialogClickListener);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mUsersChoice) {
                    case 0:{
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // If you do not have permission, request it
                            AddTrainFragment.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constants.REQUEST_STORAGE_PERMISSION);
                        } else {
                            // Launch the camera if the permission exists
                            openCamera();
                        }
                        break;
                    }
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        pickImageDialog = builder.create();
        pickImageDialog.show();
    }

    private void openGallery(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(getActivity());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                mImageUri = FileProvider.getUriForFile(getActivity(),
                        Constants.FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickImageDialog.dismiss();
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Glide.with(AddTrainFragment.this)
                        .load(mImageUri)
                        .into(binding.productDetailsGalleryImage);
            } else {
                BitmapUtils.deleteImageFile(getActivity(), mTempPhotoPath);
            }
        } else if (requestCode == Constants.PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mImageUri = data.getData();
                Glide.with(getActivity())
                        .load(mImageUri)
                        .into(binding.productDetailsGalleryImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case Constants.REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    openCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
        if(spinner.getId() == R.id.brandSpinner){
            mChosenBrand = brandList.get(position);
        } else{
            mChosenCategory = categoryList.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
