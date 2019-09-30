package com.canli.oya.traininventoryroom.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddCategoryBinding
import com.canli.oya.traininventoryroom.viewmodel.MainViewModel
import org.jetbrains.anko.toast


class AddCategoryFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoryBinding

    private val mViewModel by activityViewModels<MainViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_category, container, false)

        binding.addCategoryEditCatName.requestFocus()
        binding.addCategorySaveBtn.setOnClickListener { saveCategory() }

        return binding.root
    }

    private fun saveCategory() {
        val categoryName = binding.addCategoryEditCatName.text.toString().trim()
        if (categoryName.isBlank()) {
            context?.toast(getString(R.string.category_cannot_be_empty))
            return
        }
        val newCategory = CategoryEntry(categoryName)
        //Insert the category by the intermediance of view model
        mViewModel.insertCategory(newCategory)

        //Clear focus and hide soft keyboard
        binding.addCategoryEditCatName.text = null
        val focusedView = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusedView?.clearFocus()
        imm.hideSoftInputFromWindow(focusedView?.windowToken, 0)
    }
}
