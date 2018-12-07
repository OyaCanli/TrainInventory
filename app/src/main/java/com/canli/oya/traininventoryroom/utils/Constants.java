package com.canli.oya.traininventoryroom.utils;

public final class Constants {

    //Request codes:

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final int REQUEST_STORAGE_PERMISSION = 2;

    public static final int PICK_IMAGE_REQUEST = 3;

    //Provider name for saving the image
    public static final String FILE_PROVIDER_AUTHORITY = "com.canli.oya.traininventoryroom.fileprovider";

    //Intent extras:
    public static final String TRAIN_ID = "trainId";

    public static final String INTENT_REQUEST_CODE = "intentRequestCode";

    public static final String ALL_TRAIN = "allTrains";

    public static final String EDIT_CASE = "editCase";

    public static final String TRAINS_OF_BRAND = "trainOfBrand";

    public static final String BRAND_NAME = "brandName";

    public static final String TRAINS_OF_CATEGORY = "trainsOfCategory";

    public static final String CATEGORY_NAME = "categoryName";

    //keys for saving during rotation
    public static final String NAME_ET = "nameEditText";

    public static final String MODEL_ET = "modelEditText";

    public static final String DESCRIPTION_ET = "descriptionEditText";

    public static final String LOCATION_NUMBER_ET = "locationNumberEditText";

    public static final String LOCATION_LETTER_ET = "locationLetterEditText";

    public static final String QUANTITY_ET = "quantityEditText";

    public static final String SCALE_ET = "scaleEditText";

    public static final String BRAND_SPINNER_POSITION = "brandSpinnerPosition";

    public static final String CATEGORY_SPINNER_POSITION = "categorySpinnerPosition";

    public static final String IMAGE_URL = "imageUrl";

    public static final String UNSAVED_CHANGES = "unsavedChanges";
}
