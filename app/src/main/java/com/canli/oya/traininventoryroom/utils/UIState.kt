package com.canli.oya.traininventoryroom.utils

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.canli.oya.traininventoryroom.BR
import timber.log.Timber

class UIState(message: String,
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