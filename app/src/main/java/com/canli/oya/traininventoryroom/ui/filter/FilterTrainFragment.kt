package com.canli.oya.traininventoryroom.ui.filter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentFilterTrainBinding
import com.canli.oya.traininventoryroom.ui.base.TrainBaseAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener
import com.canli.oya.traininventoryroom.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FilterTrainFragment : Fragment(R.layout.fragment_filter_train), TrainItemClickListener,
    AdapterView.OnItemSelectedListener {

    private val binding by viewBinding(FragmentFilterTrainBinding::bind)

    private val viewModel: FilterTrainViewModel by viewModels()

    private lateinit var trainAdapter: TrainBaseAdapter

    private lateinit var intentRequest: String

    private var brandName: String? = null

    private var categoryName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            intentRequest = FilterTrainFragmentArgs.fromBundle(requireArguments()).intentRequestCode
            if (intentRequest == TRAINS_OF_BRAND) {
                brandName = FilterTrainFragmentArgs.fromBundle(requireArguments()).brandName
            }
            if (intentRequest == TRAINS_OF_CATEGORY) {
                categoryName = FilterTrainFragmentArgs.fromBundle(requireArguments()).categoryName
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainAdapter = FilteredTrainsAdapter(this)
        binding.list.adapter = trainAdapter

        binding.searchCategorySpinner.onItemSelectedListener = this
        binding.searchBrandSpinner.onItemSelectedListener = this

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryNames.collectLatest {
                    setCategorySpinner(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.brandNames.collectLatest {
                    setBrandSpinner(it)
                }
            }
        }

        binding.searchKeywords.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.keyword = s.toString()
            }
        })

        val criteriaObserver: (t: String?) -> Unit = {
            if (it != null) {
                startFiltering()
            }
        }

        viewModel.selectedBrand.observe(viewLifecycleOwner, criteriaObserver)
        viewModel.selectedCategory.observe(viewLifecycleOwner, criteriaObserver)

        binding.searchBtn.setOnClickListener {
            activity?.clearFocusAndHideKeyboard()
            startFiltering()
        }
    }

    private fun setCategorySpinner(categories : List<String>) {
        if(categories.isNotEmpty()){
            binding.searchCategorySpinner.adapter = FilterBySpinnerAdapter(
                requireContext(),
                categories,
                getString(R.string.filter_by_category)
            )
            if (intentRequest == TRAINS_OF_CATEGORY) {
                categoryName?.let { categoryName ->
                    val index = categories.indexOf(categoryName)
                    binding.searchCategorySpinner.setSelection(index)
                }
            }
        }
    }

    private fun setBrandSpinner(brands : List<String>){
        if(brands.isNotEmpty()){
            binding.searchBrandSpinner.adapter = FilterBySpinnerAdapter(
                requireContext(),
                brands,
                getString(R.string.filter_by_brand)
            )
            if (intentRequest == TRAINS_OF_BRAND) {
                brandName?.let { brandName ->
                    val index = brands.indexOf(brandName)
                    binding.searchBrandSpinner.setSelection(index)
                }
            }
        }
    }

    private fun startFiltering() {
        binding.showLoading()
        lifecycleScope.launch {
            val filteredTrains = viewModel.filterTrains()
            if (filteredTrains.isEmpty()) {
                binding.showEmpty(R.string.no_results_for_search)
                Timber.d("Filtered trains list is empty")
            } else {
                trainAdapter.submitList(filteredTrains)
                Timber.d("Filtered trains list size : ${filteredTrains.size}")
                binding.showList()
            }
        }
    }

    override fun onItemSelected(spinner: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (spinner?.id) {
            R.id.search_brandSpinner -> {
                viewModel.selectedBrand.value = if (position == 0) null
                else (spinner.adapter as FilterBySpinnerAdapter).getItem(position)
            }
            R.id.search_categorySpinner -> {
                viewModel.selectedCategory.value = if (position == 0) null
                else (spinner.adapter as FilterBySpinnerAdapter).getItem(position)
            }
        }
    }

    override fun onNothingSelected(spinner: AdapterView<*>?) {
        Timber.d("onNothingSelected is called")
        when (spinner?.id) {
            R.id.search_brandSpinner -> {
                viewModel.selectedBrand.value = null
            }
            R.id.categorySpinner -> {
                viewModel.selectedCategory.value = null
            }
        }
    }

    override fun onListItemClick(view: View, trainId: Int) {
        val action =
            FilterTrainFragmentDirections.actionFilterTrainFragmentToTrainDetailsFragment(trainId)
        binding.root.findNavController().navigate(action)
    }
}