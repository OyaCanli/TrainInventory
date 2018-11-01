package com.canli.oya.traininventory.utils;

import android.text.TextUtils;

public class DataBindingUtils {

    public static String[] splitLocation(String location) {
        return TextUtils.isEmpty(location) ? null : location.split("-");
    }

    public static String encloseInParanthesis(String category){
        return "(" + category + ")";
    }
}
