package com.canli.oya.traininventory.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canli.oya.traininventory.R;
import com.canli.oya.traininventory.data.TrainDatabase;
import com.canli.oya.traininventory.data.entities.BrandEntry;
import com.canli.oya.traininventory.data.entities.TrainEntry;
import com.canli.oya.traininventory.utils.AppExecutors;
import com.canli.oya.traininventory.utils.BitmapUtils;
import com.canli.oya.traininventory.utils.Constants;
import com.canli.oya.traininventory.utils.GlideApp;
import com.canli.oya.traininventory.viewmodel.MainViewModel;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddBrandFragment extends Fragment implements View.OnClickListener {

    private TrainDatabase mDb;
    private EditText brandName_et;
    private ImageView addPhoto_iv;
    private AlertDialog pickImageDialog;
    private String mTempPhotoPath;
    private Uri mLogoUri;
    private int mUsersChoice;
    private EditText webUrl_et;
    private boolean isUpdateCase;
    private Context mContext;
    private int mBrandId;

    private final DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
            mUsersChoice = item;
        }
    };

    public AddBrandFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_brand, container, false);

        //get database instance
        mDb = TrainDatabase.getInstance(getActivity().getApplicationContext());

        //Set click listeners
        Button save_btn = rootView.findViewById(R.id.addBrand_saveBtn);
        save_btn.setOnClickListener(this);
        addPhoto_iv = rootView.findViewById(R.id.addBrand_image);
        addPhoto_iv.setOnClickListener(this);

        brandName_et = rootView.findViewById(R.id.addBrand_editBrandName);
        webUrl_et = rootView.findViewById(R.id.addBrand_editWeb);
        //Request focus on the first edittext
        brandName_et.requestFocus();

        final MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.INTENT_REQUEST_CODE)) { //This is the "edit" case
            isUpdateCase = true;
            viewModel.getChosenBrand().observe(getActivity(), new Observer<BrandEntry>() {
                @Override
                public void onChanged(@Nullable BrandEntry brandEntry) {
                    populateFields(brandEntry);
                    mBrandId = brandEntry.getBrandId();
                }
            });
        }

        return rootView;
    }

    private void populateFields(BrandEntry brand){
        brandName_et.setText(brand.getBrandName());
        webUrl_et.setText(brand.getWebUrl());
        String logoUriString = brand.getBrandLogoUri();
        if(logoUriString != null){
            mLogoUri = Uri.parse(logoUriString);
        }
        if(mContext != null){
            GlideApp.with(mContext)
                    .load(brand.getBrandLogoUri())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(addPhoto_iv);
        }
    }

    @Override
    public void onClick(View v) {
        //If save is clicked
        if (v.getId() == R.id.addBrand_saveBtn) {
            saveBrand();
        } else {
            //If add photo is clicked
            openImageDialog();
        }
    }

    private void saveBrand() {
        //Get brand name from edittext
        String brandName = brandName_et.getText().toString().trim();

        //Get web address from edittext
        String webAddress = webUrl_et.getText().toString().trim();

        //If there is a uri for logo image, parse it to string
        String imagePath = null;
        if (mLogoUri != null) {
            imagePath = mLogoUri.toString();
        }

        if(isUpdateCase){
            //Construct a new BrandEntry object from this data with ID included
            final BrandEntry brandToUpdate = new BrandEntry(mBrandId, brandName, imagePath, webAddress);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.brandDao().updateBrandInfo(brandToUpdate);
                }
            });
        } else{
            //Construct a new BrandEntry object from this data (without ID)
            final BrandEntry newBrand = new BrandEntry(brandName, imagePath, webAddress);
            //Insert to database in a background thread
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.brandDao().insertBrand(newBrand);
                }
            });
        }

        Toast.makeText(getActivity(), R.string.brand_Saved, Toast.LENGTH_SHORT).show();

        //Remove fragment
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

    private void openImageDialog() {

        //Opens a dialog which lets the user choose either adding a photo from gallery or taking a new picture.
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
                            AddBrandFragment.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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

                mLogoUri = FileProvider.getUriForFile(getActivity(),
                        Constants.FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLogoUri);
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
                Glide.with(mContext)
                        .load(mLogoUri)
                        .into(addPhoto_iv);
            } else {
                BitmapUtils.deleteImageFile(getActivity(), mTempPhotoPath);
            }
        } else if (requestCode == Constants.PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mLogoUri = data.getData();
                Glide.with(mContext)
                        .load(mLogoUri)
                        .into(addPhoto_iv);
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
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}
