package com.canli.oya.traininventory.utils;

public final class Constants {

    //Request codes:

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final int REQUEST_STORAGE_PERMISSION = 2;

    public static final int PICK_IMAGE_REQUEST = 3;

    //Provider name for saving the image
    public static final String FILE_PROVIDER_AUTHORITY = "com.canli.oya.traininventory.fileprovider";

    //Intent extras:
    public static final String TRAIN_ID = "trainId";

    public static final String INTENT_REQUEST_CODE = "intentRequestCode";

    public static final String EDIT_CASE = "editCase";

    public static final String TRAINS_OF_BRAND = "trainOfBrand";

    public static final String BRAND_NAME = "brandName";

    public static final String TRAINS_OF_CATEGORY = "trainsOfCategory";

    public static final String CATEGORY_NAME = "categoryName";

    //Tags
    public static final String TAG_TRAINS = "trainsList";

    public static final String TAG_BRANDS = "brandList";

    public static final String TAG_CATEGORIES = "categoryList";

    public static final String TAG_DETAILS = "trainDetails";

    public static final String TAG_ADD_TRAIN = "addTrain";
}
