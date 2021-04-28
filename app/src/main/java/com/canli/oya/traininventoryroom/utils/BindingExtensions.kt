package com.canli.oya.traininventoryroom.utils

import android.view.View
import androidx.annotation.StringRes
import com.canli.oya.traininventoryroom.databinding.FragmentFilterTrainBinding
import com.canli.oya.traininventoryroom.databinding.FragmentListBinding


fun FragmentListBinding.showLoading() {
    loadingIndicator.visibility = View.VISIBLE
    emptyImage.visibility = View.GONE
    emptyText.visibility = View.GONE
    list.visibility = View.GONE
}

fun FragmentFilterTrainBinding.showLoading() {
    loadingIndicator.visibility = View.VISIBLE
    emptyImage.visibility = View.GONE
    emptyText.visibility = View.GONE
    list.visibility = View.GONE
}

fun FragmentListBinding.showEmpty(@StringRes emptyMessage: Int) {
    list.visibility = View.GONE
    loadingIndicator.visibility = View.GONE
    emptyImage.visibility = View.VISIBLE
    emptyText.setText(emptyMessage)
    emptyText.visibility = View.VISIBLE
}

fun FragmentFilterTrainBinding.showEmpty(@StringRes emptyMessage: Int) {
    list.visibility = View.GONE
    loadingIndicator.visibility = View.GONE
    emptyImage.visibility = View.VISIBLE
    emptyText.setText(emptyMessage)
    emptyText.visibility = View.VISIBLE
}

fun FragmentListBinding.showList() {
    list.visibility = View.VISIBLE
    loadingIndicator.visibility = View.GONE
    emptyImage.visibility = View.GONE
    emptyText.visibility = View.GONE
}

fun FragmentFilterTrainBinding.showList() {
    list.visibility = View.VISIBLE
    loadingIndicator.visibility = View.GONE
    emptyImage.visibility = View.GONE
    emptyText.visibility = View.GONE
}

