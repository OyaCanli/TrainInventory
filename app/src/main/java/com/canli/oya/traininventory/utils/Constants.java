package com.canli.oya.traininventory.utils;

public final class Constants {

    //Request codes:

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final int REQUEST_STORAGE_PERMISSION = 2;

    public static final int PICK_IMAGE_REQUEST = 3;

    //Provider name for saving the image
    public static final String FILE_PROVIDER_AUTHORITY = "com.canli.oya.traininventory.fileprovider";

    //Intent extras:
    public static final String INTENT_REQUEST_CODE = "intentRequestCode";

    public static final String EDIT_CASE = "editCase";

    //Time constants for transitions
    public static final long MOVE_DEFAULT_TIME = 1000;

    public static final long FADE_DEFAULT_TIME = 300;
}
