package com.canli.oya.traininventoryroom.ui.categories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.common.INTENT_REQUEST_CODE
import com.canli.oya.traininventoryroom.data.CategoryEntry
import com.canli.oya.traininventoryroom.databinding.FragmentAddCategoryBinding
import com.canli.oya.traininventoryroom.di.TrainApplication
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import org.jetbrains.anko.toast
import javax.inject.Inject


class AddCategoryFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoryBinding

    private lateinit var viewModel : CategoryViewModel

    @Inject
    lateinit var viewModelFactory : TrainInventoryVMFactory

    private var mCategoryId: Int = 0
    private var isEditCase: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_category, container, false)

        binding.addCategoryEditCatName.requestFocus()
        binding.addCategorySaveBtn.setOnClickListener { saveCategory() }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity?.application as TrainApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(parentFragment!!, viewModelFactory).get(CategoryViewModel::class.java)

        if (arguments?.containsKey(INTENT_REQUEST_CODE) == true) { //This is the "edit" case
            isEditCase = true
            viewModel.chosenItem.observe(viewLifecycleOwner, Observer { categoryEntry ->
                categoryEntry?.let {
                    binding.chosenCategory = it
                    mCategoryId = it.categoryId
                }
            })
        }
    }

    private fun saveCategory() {
        val categoryName = binding.addCategoryEditCatName.text.toString().trim()
        if (categoryName.isBlank()) {
            context?.toast(getString(R.string.category_cannot_be_empty))
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

        //Clear focus and hide soft keyboard
        binding.addCategoryEditCatName.text = null
        val focusedView = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusedView?.clearFocus()
        imm.hideSoftInputFromWindow(focusedView?.windowToken, 0)
    }
}
