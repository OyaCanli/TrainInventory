package com.canli.oya.traininventory.ui;

import android.Manifest;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.adapters.CustomSpinAdapter;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.databinding.FragmentAddTrainBinding;
import com.canli.oya.traininventory.utils.BitmapUtils;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.GlideApp;
import com.canli.oya.traininventory.utils.InjectorUtils;
import com.canli.oya.traininventory.viewmodel.ChosenTrainFactory;
import com.canli.oya.traininventory.viewmodel.ChosenTrainViewModel;
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
    private AlertDialog pickImageDialog;
    private String mTempPhotoPath;
    private String mImageUri;
    private int mUsersChoice;
    private List<String> categoryList;
    private List<BrandEntry> brandList;
    private int mTrainId;
    private UnsavedChangesListener mCallback;
    private MainViewModel mViewModel;
    private TrainEntry mChosenTrain;
    private boolean isEdit;
    private boolean brandsLoaded, categoryLoaded, trainLoaded;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mCallback.warnForUnsavedChanges(true);
        }
    };

    private final DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
            mUsersChoice = item;
        }
    };

    public AddTrainFragment() {
    }

    public interface UnsavedChangesListener {
        void warnForUnsavedChanges(boolean shouldWarn);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (UnsavedChangesListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UnsavedChangesListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_train, container, false);

        setHasOptionsMenu(true);

        //Set click listener on buttons
        binding.addTrainAddBrandBtn.setOnClickListener(this);
        binding.addTrainAddCategoryBtn.setOnClickListener(this);
        binding.productDetailsGalleryImage.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TrainDatabase database = TrainDatabase.getInstance(getActivity().getApplicationContext());

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mViewModel.loadBrandList(InjectorUtils.provideBrandRepo(getContext()));
        mViewModel.loadCategoryList(InjectorUtils.provideCategoryRepo(getContext()));

        //Set brand spinner
        brandList = new ArrayList<>();
        final CustomSpinAdapter brandAdapter = new CustomSpinAdapter(getActivity(), brandList);
        binding.brandSpinner.setAdapter(brandAdapter);
        binding.brandSpinner.setOnItemSelectedListener(this);
        mViewModel.getBrandList().observe(AddTrainFragment.this, new Observer<List<BrandEntry>>() {
            @Override
            public void onChanged(@Nullable List<BrandEntry> brandEntries) {
                brandList.clear();
                brandList.addAll(brandEntries);
                brandAdapter.notifyDataSetChanged();
                brandsLoaded = true;
                setSpinners();
            }
        });

        //Set category spinner
        categoryList = new ArrayList<>();
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
        binding.categorySpinner.setOnItemSelectedListener(this);
        mViewModel.getCategoryList().observe(AddTrainFragment.this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> categoryEntries) {
                categoryList.clear();
                categoryList.addAll(categoryEntries);
                categoryAdapter.notifyDataSetChanged();
                categoryLoaded = true;
                setSpinners();
            }
        });

        Bundle bundle = getArguments();
        //"Edit" case
        if (bundle != null && bundle.containsKey(Constants.TRAIN_ID)) {
            getActivity().setTitle(getString(R.string.edit_train));
            isEdit = true;
            mTrainId = bundle.getInt(Constants.TRAIN_ID);
            //This view model is instantiated only in edit mode. It contains the chosen train. It is attached to this fragment
            ChosenTrainFactory factory = new ChosenTrainFactory(database, mTrainId);
            final ChosenTrainViewModel viewModel = ViewModelProviders.of(this, factory).get(ChosenTrainViewModel.class);
            viewModel.getChosenTrain().observe(this, new Observer<TrainEntry>() {
                @Override
                public void onChanged(@Nullable TrainEntry trainEntry) {
                    binding.setChosenTrain(trainEntry);
                    binding.executePendingBindings();
                    mChosenTrain = trainEntry;
                    trainLoaded = true;
                    setSpinners();
                    if(savedInstanceState != null) {
                        restoreState(savedInstanceState);
                    }
                }
            });

            setTouchListenersToEditTexts();
        } else { //This is the "add" case
            getActivity().setTitle(getString(R.string.add_train));
            binding.executePendingBindings();
            isEdit = false;
            setChangeListenersToEdittexts();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && !isEdit) {
            restoreState(savedInstanceState);
        }
    }

    private void restoreState(@NonNull Bundle savedInstanceState) {
        binding.brandSpinner.setSelection(savedInstanceState.getInt(Constants.BRAND_SPINNER_POSITION));
        binding.categorySpinner.setSelection(savedInstanceState.getInt(Constants.CATEGORY_SPINNER_POSITION));
        mImageUri = savedInstanceState.getString(Constants.IMAGE_URL);
        GlideApp.with(AddTrainFragment.this)
                .load(mImageUri)
                .placeholder(R.drawable.ic_gallery)
                .into(binding.productDetailsGalleryImage);
        binding.editTrainName.setText(savedInstanceState.getString(Constants.NAME_ET));
        binding.editTrainDescription.setText(savedInstanceState.getString(Constants.DESCRIPTION_ET));
        binding.editReference.setText(savedInstanceState.getString(Constants.MODEL_ET));
        binding.editQuantity.setText(savedInstanceState.getString(Constants.QUANTITY_ET));
        binding.editLocationLetter.setText(savedInstanceState.getString(Constants.LOCATION_LETTER_ET));
        binding.editLocationNumber.setText(savedInstanceState.getString(Constants.LOCATION_NUMBER_ET));
        binding.editScale.setText(savedInstanceState.getString(Constants.SCALE_ET));
    }

    private void setSpinners() {
        if(trainLoaded && categoryLoaded && brandsLoaded){
            //Set category spinner
            binding.categorySpinner.setSelection(categoryList.indexOf(mChosenTrain.getCategoryName()));
            //Set brand spinner
            int brandIndex = 0;
            for (int i = 0; i < brandList.size(); i++) {
                if (brandList.get(i).getBrandName().equals(mChosenTrain.getBrandName())) {
                    brandIndex = i;
                }
            }
            binding.brandSpinner.setSelection(brandIndex);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_with_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveTrain();
        } else if(id == android.R.id.home){
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

        if (mTrainId == 0) {
            //If this is a new train
            final TrainEntry newTrain = new TrainEntry(trainName, reference, mChosenBrand, mChosenCategory, quantity, mImageUri, description, location, scale);
            mViewModel.insertTrain(newTrain);
        } else {
            //If this is a train that already exist
            final TrainEntry trainToUpdate = new TrainEntry(mTrainId, trainName, reference, mChosenBrand, mChosenCategory, quantity, mImageUri, description, location, scale);
            mViewModel.updateTrain(trainToUpdate);
        }
        //After adding the train, go back to where user come from.
        mCallback.warnForUnsavedChanges(false);
        getActivity().onBackPressed();
    }

    private void openImageDialog() {
        String[] dialogOptions = getActivity().getResources().getStringArray(R.array.dialog_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image_from);
        builder.setSingleChoiceItems(dialogOptions, -1, mDialogClickListener);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                GlideApp.with(AddTrainFragment.this)
                        .load(mImageUri)
                        .placeholder(R.drawable.ic_gallery)
                        .into(binding.productDetailsGalleryImage);
            } else {
                BitmapUtils.deleteImageFile(getActivity(), mTempPhotoPath);
            }
        } else if (requestCode == Constants.PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                mImageUri = imageUri.toString();
                GlideApp.with(AddTrainFragment.this)
                        .load(mImageUri)
                        .placeholder(R.drawable.ic_gallery)
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.NAME_ET, binding.editTrainName.getText().toString());
        outState.putString(Constants.DESCRIPTION_ET, binding.editTrainDescription.getText().toString());
        outState.putString(Constants.MODEL_ET, binding.editReference.getText().toString());
        outState.putString(Constants.QUANTITY_ET, binding.editQuantity.getText().toString());
        outState.putString(Constants.SCALE_ET, binding.editScale.getText().toString());
        outState.putString(Constants.LOCATION_NUMBER_ET, binding.editLocationNumber.getText().toString());
        outState.putString(Constants.LOCATION_LETTER_ET, binding.editLocationLetter.getText().toString());
        outState.putInt(Constants.BRAND_SPINNER_POSITION, binding.brandSpinner.getSelectedItemPosition());
        outState.putInt(Constants.CATEGORY_SPINNER_POSITION, binding.categorySpinner.getSelectedItemPosition());
        outState.putString(Constants.IMAGE_URL, mImageUri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback.warnForUnsavedChanges(false);
    }

    private void setChangeListenersToEdittexts() {
        //Set change listeners on edit texts
        binding.editReference.addTextChangedListener(mTextWatcher);
        binding.editTrainName.addTextChangedListener(mTextWatcher);
        binding.editTrainDescription.addTextChangedListener(mTextWatcher);
        binding.editLocationNumber.addTextChangedListener(mTextWatcher);
        binding.editLocationLetter.addTextChangedListener(mTextWatcher);
        binding.editScale.addTextChangedListener(mTextWatcher);
        binding.editQuantity.addTextChangedListener(mTextWatcher);
    }

    private void setTouchListenersToEditTexts() {
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCallback.warnForUnsavedChanges(true);
                return false;
            }
        };

        //Set change listeners on edit texts
        binding.editReference.setOnTouchListener(touchListener);
        binding.editTrainName.setOnTouchListener(touchListener);
        binding.editTrainDescription.setOnTouchListener(touchListener);
        binding.editLocationNumber.setOnTouchListener(touchListener);
        binding.editLocationLetter.setOnTouchListener(touchListener);
        binding.editScale.setOnTouchListener(touchListener);
        binding.editQuantity.setOnTouchListener(touchListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isEdit) {
            removeTextWatchers();
        } else {
            removeTouchListeners();
        }
    }

    private void removeTextWatchers() {
        binding.editReference.removeTextChangedListener(mTextWatcher);
        binding.editTrainName.removeTextChangedListener(mTextWatcher);
        binding.editTrainDescription.removeTextChangedListener(mTextWatcher);
        binding.editLocationNumber.removeTextChangedListener(mTextWatcher);
        binding.editLocationLetter.removeTextChangedListener(mTextWatcher);
        binding.editScale.removeTextChangedListener(mTextWatcher);
        binding.editQuantity.removeTextChangedListener(mTextWatcher);
    }

    private void removeTouchListeners() {
        binding.editReference.setOnTouchListener(null);
        binding.editTrainName.setOnTouchListener(null);
        binding.editTrainDescription.setOnTouchListener(null);
        binding.editLocationNumber.setOnTouchListener(null);
        binding.editLocationLetter.setOnTouchListener(null);
        binding.editScale.setOnTouchListener(null);
        binding.editQuantity.setOnTouchListener(null);
    }
}
