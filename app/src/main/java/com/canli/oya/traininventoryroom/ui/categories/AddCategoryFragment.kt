package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentAddCategoryBinding
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.ui.base.setMenuIcon
import com.canli.oya.traininventoryroom.utils.IS_EDIT
import com.canli.oya.traininventoryroom.utils.shortToast
import com.canlioya.core.models.Category
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddCategoryFragment : Fragment(R.layout.fragment_add_category) {

    private val binding by viewBinding(FragmentAddCategoryBinding::bind)

    private val viewModel : CategoryViewModel by viewModels({requireParentFragment()})

    private var mCategoryId: Int = 0
    private var isEditCase: Boolean = false

    private var categoryList : List<String?> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addCategoryEditCatName.requestFocus()
        binding.addCategorySaveBtn.setOnClickListener { saveCategory() }

        if (arguments?.getBoolean(IS_EDIT) == true) { //This is the "edit" case
            isEditCase = true
            viewModel.chosenItem.observe(viewLifecycleOwner, { categoryEntry ->
                categoryEntry?.let {
                    binding.chosenCategory = it
                    mCategoryId = it.categoryId
                }
            })
        }

        lifecycleScope.launch {
            viewModel.allItems.collectLatest { categoryEntries ->
                categoryList = categoryEntries.map { it.categoryName }
            }
        }
    }

    private fun saveCategory() {
        //Validate category name
        val categoryName = binding.addCategoryEditCatName.text.toString().trim()
        if (categoryName.isBlank()) {
            context?.shortToast(getString(R.string.category_cannot_be_empty))
            return
        }

        if(categoryList.contains(categoryName)){
            context?.shortToast(getString(R.string.category_already_exists))
            return
        }

        if(isEditCase){
            val categoryToUpdate = Category(mCategoryId, categoryName)
            viewModel.updateItem(categoryToUpdate)
        } else {
            val newCategory = Category(categoryName = categoryName)
            //Insert the category by the intermediance of view model
            viewModel.insertItem(newCategory)
        }

        clearFocusAndHideSoftKeyboard()

        if(isEditCase || parentFragment is AddTrainFragment){
            removeFragment()
        }
    }

    private fun clearFocusAndHideSoftKeyboard() {
        //Clear focus and hide soft keyboard
        binding.addCategoryEditCatName.text = null
        val focusedView = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusedView?.clearFocus()
        imm.hideSoftInputFromWindow(focusedView?.windowToken, 0)
    }

    private fun removeFragment() {
        val currentInstance = if(parentFragment is AddTrainFragment) parentFragmentManager.findFragmentById(R.id.childFragContainer)
                            else parentFragmentManager.findFragmentById(R.id.list_addFrag_container)

        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            currentInstance?.let {
                remove(it)
            }
        }

        if(parentFragment is CategoryListFragment) {
            (parentFragment as CategoryListFragment).addMenuItem?.setMenuIcon(R.drawable.avd_plus_to_cross)
        }
    }
}
