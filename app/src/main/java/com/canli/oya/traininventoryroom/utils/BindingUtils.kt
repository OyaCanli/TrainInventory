@file:JvmName("BindingUtils")

package com.canli.oya.traininventoryroom.utils

fun encloseInParenthesis(category: String?): String? {
    return category?.let {"($it)"}
}