@file:JvmName("DataBindingUtils")

package com.canli.oya.traininventoryroom.utils

fun splitLocation(location: String?): List<String>? {
    return if (location.isNullOrBlank()) null
            else location.split("-")
}

fun encloseInParanthesis(category: String): String {
    return "($category)"
}