package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddCategoryBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.IS_EDIT
import com.canli.oya.traininventoryroom.utils.shortToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class AddCategoryFragment : Fragment(R.layout.fragment_add_category) {

    private val binding by viewBinding(FragmentAddCategoryBinding::bind)

    private lateinit var viewModel : CategoryViewModel

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private var mCategoryId: Int = 0
    private var isEditCase: Boolean = false

    private var categoryList : List<String?> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addCategoryEditCatName.requestFocus()
        binding.addCategorySaveBtn.setOnClickListener { saveCategory() }

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        viewModel = ViewModelProvider(requireParentFragment(), viewModelFactory).get(CategoryViewModel::class.java)

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
            //TODO : Toasts are not visible enough because of open soft keyboard. Customize toasts or replace with snacks
            return
        }

        if(isEditCase){
            val categoryToUpdate = CategoryEntry(mCategoryId, categoryName)
            viewModel.updateItem(categoryToUpdate)
        } else {
            val newCategory = CategoryEntry(categoryName = categoryName)
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
    }
}
