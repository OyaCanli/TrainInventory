package com.canli.oya.traininventoryroom.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddCategoryBinding
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel


class AddCategoryFragment : Fragment() {

    private var binding: FragmentAddCategoryBinding? = null
    private var mViewModel: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentAddCategoryBinding>(
                inflater, R.layout.fragment_add_category, container, false)

        binding!!.addCategoryEditCatName.requestFocus()
        binding!!.addCategorySaveBtn.setOnClickListener { saveCategory() }

        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    private fun saveCategory() {
        val categoryName = binding!!.addCategoryEditCatName.text.toString().trim { it <= ' ' }
        val newCategory = CategoryEntry(categoryName)
        //Insert the category by the intermediance of view model
        mViewModel!!.insertCategory(newCategory)

        //Remove the fragment
        val parentFrag = parentFragment
        val currentInstance: Fragment?
        if (parentFrag is AddTrainFragment) {
            currentInstance = fragmentManager!!.findFragmentById(R.id.childFragContainer)
        } else {
            currentInstance = fragmentManager!!.findFragmentById(R.id.brandlist_addFrag_container)
        }

        val focusedView = activity!!.currentFocus
        if (focusedView != null) {
            focusedView.clearFocus()
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }

        fragmentManager!!.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(currentInstance!!)
                .commit()

    }

    /*override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        return if (!enter && parentFragment is CategoryListFragment) dummyAnimation
               else super.onCreateAnimation(transit, enter, nextAnim)
    }

    companion object {

        *//*This is for solving the weird behaviour of child fragments during exit.
    I found this solution from this SO entry and adapted to my case:
    https://stackoverflow.com/questions/14900738/nested-fragments-disappear-during-transition-animation*//*
        private val dummyAnimation = AlphaAnimation(1f, 1f)

        init {
            dummyAnimation.duration = 500
        }
    }*/
}
