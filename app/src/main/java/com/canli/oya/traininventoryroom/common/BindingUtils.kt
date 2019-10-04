@file:JvmName("BindingUtils")

package com.canli.oya.traininventoryroom.common

fun encloseInParenthesis(category: String?): String? {
    return category?.let {"($it)"}
}

fun attachLocationString(locationRow: String?, locationColumn : String?) : String? {
    val row = locationRow ?: ""
    val column = locationColumn ?: ""
    return if(row.isNotBlank() && column.isNotBlank()){
        "$row - $column"
    } else {
        "$row $column"
    }
}