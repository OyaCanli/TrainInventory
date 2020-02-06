package com.canli.oya.traininventoryroom.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.TrainEntry
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.ui.brands.BrandListFragment
import com.canli.oya.traininventoryroom.ui.categories.CategoryListFragment
import com.canli.oya.traininventoryroom.ui.exportToExcel.ExportingToExcelDialog
import com.canli.oya.traininventoryroom.ui.trains.TrainDetailsFragment
import com.canli.oya.traininventoryroom.ui.trains.TrainListFragment
import com.canli.oya.traininventoryroom.utils.*

open class Navigator {

    var fragmentManager : FragmentManager? = null

    fun launchCategoryList() = commitFragmentTransaction(CategoryListFragment())

    fun launchBrandList() = commitFragmentTransaction(BrandListFragment())

    fun launchTrainList(){
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, ALL_TRAIN)
        trainListFrag.arguments = args
        commitFragmentTransaction(trainListFrag)
    }

    fun launchTrainList_withThisCategory(categoryName : String){
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_CATEGORY)
        args.putString(CATEGORY_NAME, categoryName)
        trainListFrag.arguments = args
        commitFragmentTransaction(trainListFrag)
    }

    fun launchTrainList_withThisBrand(brandName : String){
        val trainListFrag = TrainListFragment()
        val args = Bundle()
        args.putString(INTENT_REQUEST_CODE, TRAINS_OF_BRAND)
        args.putString(BRAND_NAME, brandName)
        trainListFrag.arguments = args
        commitFragmentTransaction(trainListFrag)
    }

    fun launchTrainDetails(trainId : Int){
        val trainDetailsFrag = TrainDetailsFragment()
        val args = Bundle()
        args.putInt(TRAIN_ID, trainId)
        trainDetailsFrag.arguments = args
        commitFragmentTransaction(trainDetailsFrag)
    }

    fun launchAddTrain(){
        val addTrainFrag = AddTrainFragment()
        val args = Bundle()
        args.putBoolean(IS_EDIT, false)
        addTrainFrag.arguments = args
        commitFragmentTransaction(addTrainFrag)
    }

    fun launchEditTrain(chosenTrain : TrainEntry){
        val editTrainFrag = AddTrainFragment()
        val args = Bundle()
        args.putBoolean(IS_EDIT, true)
        args.putParcelable(CHOSEN_TRAIN, chosenTrain)
        editTrainFrag.arguments = args
        commitFragmentTransaction(editTrainFrag)
    }

    private fun commitFragmentTransaction(fragment : Fragment){
        fragmentManager?.commit { replace(R.id.container, fragment)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)}
    }

    fun launchExportToExcelFragment(){
        val dialogFrag = ExportingToExcelDialog()
        fragmentManager?.let {
            dialogFrag.show(it, null)
        }
    }


}