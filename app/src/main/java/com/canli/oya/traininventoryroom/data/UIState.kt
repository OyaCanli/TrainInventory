package com.canli.oya.traininventoryroom.data

import androidx.annotation.StringRes
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.canli.oya.traininventoryroom.BR
import com.canli.oya.traininventoryroom.R
import timber.log.Timber

class UIState(@StringRes message: Int = R.string.no_items_found,
              loading : Boolean = true,
              success: Boolean = false,
              empty: Boolean = false): BaseObservable(){

    @get:Bindable
    var showLoading = loading
        set(value) {
            Timber.d( "showLoading is set to: $value")
            field = value
            if(value){
                showEmpty = false
                showList = false
            }
            notifyPropertyChanged(BR.showLoading)
        }

    @get:Bindable
    var showEmpty = empty
        set(value) {
            Timber.d( "showEmpty is set to: $value")
            field = value
            if(value){
                showLoading = false
                showList = false
            }
            notifyPropertyChanged(BR.showEmpty)
        }

    @get:Bindable
    var emptyMessage = message
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessage)
        }

    @get:Bindable
    var showList = success
        set(value) {
            Timber.d( "showList is set to: $value")
            field = value
            if(value){
                showLoading = false
                showEmpty = false
            }
            notifyPropertyChanged(BR.showList)
        }

}