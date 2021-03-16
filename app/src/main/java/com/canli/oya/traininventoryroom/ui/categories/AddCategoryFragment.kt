package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddCategoryBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.addtrain.AddTrainFragment
import com.canli.oya.traininventoryroom.utils.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.utils.shortToast
import javax.inject.Inject


class AddCategoryFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoryBinding

    private lateinit var viewModel : CategoryViewModel

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private var mCategoryId: Int = 0
    private var isEditCase: Boolean = false

    private var categoryList : List<String?> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_category, container, false)

        binding.addCategoryEditCatName.requestFocus()
        binding.addCategorySaveBtn.setOnClickListener { saveCategory() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        viewModel = ViewModelProvider(requireParentFragment(), viewModelFactory).get(CategoryViewModel::class.java)

        if (arguments?.containsKey(INTENT_REQUEST_CODE) == true) { //This is the "edit" case
            isEditCase = true
            viewModel.chosenItem.observe(viewLifecycleOwner, { categoryEntry ->
                categoryEntry?.let {
                    binding.chosenCategory = it
                    mCategoryId = it.categoryId
                }
            })
        }

        viewModel.allItems.observe(viewLifecycleOwner, { categoryEntries ->
            categoryEntries?.let {
                categoryList = it.map { categoryEntry -> categoryEntry.categoryName }
            }
        })
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

        if(parentFragment is AddTrainFragment){
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
        val currentInstance = parentFragmentManager.findFragmentById(R.id.childFragContainer)
        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            remove(currentInstance!!)
        }
    }
}
