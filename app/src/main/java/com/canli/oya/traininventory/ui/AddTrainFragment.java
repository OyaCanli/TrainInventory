package com.canli.oya.traininventory.ui;

import android.Manifest;
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
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.CustomSpinAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.CategoryEntry;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.databinding.FragmentAddTrainBinding;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.BitmapUtils;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.GlideApp;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddTrainFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private FragmentAddTrainBinding binding;
    private String mChosenCategory;
    private String mChosenBrand;
    private TrainDatabase mDb;
    private AlertDialog pickImageDialog;
    private String mTempPhotoPath;
    private String mImageUri;
    private int mUsersChoice;
    private List<String> categoryList;
    private List<BrandEntry> brandList;
    private int mTrainId;

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

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        final MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)) { //This is the "edit" case
            viewModel.getChosenTrain().observe(this, new Observer<TrainEntry>() {
                @Override
                public void onChanged(@Nullable TrainEntry trainEntry) {
                    populateFields(trainEntry);
                    mTrainId = trainEntry.getTrainId();
                }
            });
            getActivity().setTitle(getString(R.string.edit_train));
        } else {
            getActivity().setTitle(getString(R.string.add_train));
        }

        //Set category spinner
        categoryList = new ArrayList<>();
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
        binding.categorySpinner.setOnItemSelectedListener(this);
        viewModel.getCategoryList().observe(getActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> categoryEntries) {
                categoryList.clear();
                categoryList.addAll(categoryEntries);
                categoryAdapter.notifyDataSetChanged();
            }
        });


        //Set brand spinner
        brandList = new ArrayList<>();
        final CustomSpinAdapter brandAdapter = new CustomSpinAdapter(getActivity(), brandList);
        binding.brandSpinner.setAdapter(brandAdapter);
        binding.brandSpinner.setOnItemSelectedListener(this);
        viewModel.getBrandList().observe(this, new Observer<List<BrandEntry>>() {
            @Override
            public void onChanged(@Nullable List<BrandEntry> brandEntries) {
                brandList.clear();
                brandList.addAll(brandEntries);
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

    private void populateFields(TrainEntry trainToEdit) {
        binding.brandSpinner.setSelection(categoryList.indexOf(trainToEdit.getCategoryName()));
        binding.brandSpinner.setSelection(brandList.indexOf(trainToEdit.getBrandName()));
        binding.editReference.setText(trainToEdit.getModelReference());
        binding.editTrainName.setText(trainToEdit.getTrainName());
        binding.editQuantity.setText(String.valueOf(trainToEdit.getQuantity()));
        binding.editTrainDescription.setText(trainToEdit.getDescription());
        mImageUri = trainToEdit.getImageUri();
        GlideApp.with(AddTrainFragment.this)
                .load(trainToEdit.getImageUri())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.productDetailsGalleryImage);
        String location = trainToEdit.getLocation();
        if(location.contains("-") && location.length()>1){
            String[] locationParts = trainToEdit.getLocation().split("-");
            binding.editLocationNumber.setText(locationParts[0]);
            binding.editLocationLetter.setText(locationParts[1]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn: {
                saveTrain();
                break;
            }
            case R.id.addTrain_addBrandBtn: {
                insertAddBrandFragment();
                break;
            }
            case R.id.addTrain_addCategoryBtn: {
                insertAddCategoryFragment();
                break;
            }
            case R.id.product_details_gallery_image: {
                openImageDialog();
                break;
            }
        }
    }

    private void insertAddCategoryFragment() {
        AddCategoryFragment addCatFrag = new AddCategoryFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.childFragContainer, addCatFrag)
                .commit();
    }

    private void insertAddBrandFragment() {
        AddBrandFragment addBrandFrag = new AddBrandFragment();
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.translate_from_top, 0)
                .replace(R.id.childFragContainer, addBrandFrag)
                .commit();
    }

    private void saveTrain() {
        String quantityToParse = binding.editQuantity.getText().toString().trim();
        //Quantity can be null. But if it is not null it should be a positive integer
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityToParse)) {
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
        String scale = binding.editScale.getText().toString().trim();

        if(mTrainId == 0) {
            //If this is a new train
            final TrainEntry newTrain = new TrainEntry(trainName, reference, mChosenBrand, mChosenCategory, quantity, mImageUri, description, location, scale);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.trainDao().insertTrain(newTrain);
                }
            });
        } else{
            //If this is a train that already exist
            final TrainEntry trainToUpdate = new TrainEntry(mTrainId, trainName, reference, mChosenBrand, mChosenCategory, quantity, mImageUri, description, location, scale);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.trainDao().updateTrainInfo(trainToUpdate);
                }
            });
        }
        TrainListFragment trainListFrag = new TrainListFragment();
        trainListFrag.setEnterTransition(new Slide(Gravity.END));
        trainListFrag.setExitTransition(new Slide(Gravity.START));
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
                    case 0: {
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

    private void openGallery() {
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

                Uri imageUri = FileProvider.getUriForFile(getActivity(),
                        Constants.FILE_PROVIDER_AUTHORITY,
                        photoFile);
                mImageUri = imageUri.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
                Uri imageUri = data.getData();
                mImageUri = imageUri.toString();
                Glide.with(AddTrainFragment.this)
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
        if (spinner.getId() == R.id.brandSpinner) {
            mChosenBrand = brandList.get(position).getBrandName();
        } else {
            mChosenCategory = categoryList.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
