package com.canli.oya.traininventoryroom.ui.filter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canli.oya.traininventoryroom.R
import com.canli.oya.traininventoryroom.databinding.FragmentFilterTrainBinding
import com.canli.oya.traininventoryroom.di.ComponentProvider
import com.canli.oya.traininventoryroom.di.TrainInventoryVMFactory
import com.canli.oya.traininventoryroom.ui.base.TrainBaseAdapter
import com.canli.oya.traininventoryroom.ui.common.TrainItemClickListener
import com.canli.oya.traininventoryroom.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FilterTrainFragment : Fragment(R.layout.fragment_filter_train), TrainItemClickListener,
    AdapterView.OnItemSelectedListener {

    private val binding by viewBinding(FragmentFilterTrainBinding::bind)

    private lateinit var viewModel: FilterTrainViewModel

    private lateinit var trainAdapter: TrainBaseAdapter

    @Inject
    lateinit var viewModelFactory: TrainInventoryVMFactory

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

        ComponentProvider.getInstance(requireActivity().application).daggerComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FilterTrainViewModel::class.java)

        trainAdapter = FilteredTrainsAdapter(this)
        binding.list.adapter = trainAdapter

        binding.searchCategorySpinner.onItemSelectedListener = this
        binding.searchBrandSpinner.onItemSelectedListener = this

        lifecycleScope.launch {
            val categoryList = async(Dispatchers.IO) {
                viewModel.getCategoryNames()
            }
            val brandList = async(Dispatchers.IO) {
                viewModel.getBrandNames()
            }
            binding.searchCategorySpinner.adapter = FilterBySpinnerAdapter(
                requireContext(),
                categoryList.await(),
                getString(R.string.filter_by_category)
            )
            binding.searchBrandSpinner.adapter = FilterBySpinnerAdapter(
                requireContext(),
                brandList.await(),
                getString(R.string.filter_by_brand)
            )

            when (intentRequest) {
                TRAINS_OF_CATEGORY -> {
                    categoryName?.let {
                        val index = categoryList.await().indexOf(it)
                        binding.searchCategorySpinner.setSelection(index)
                    }
                }
                TRAINS_OF_BRAND -> {
                    brandName?.let {
                        val index = brandList.await().indexOf(it)
                        binding.searchBrandSpinner.setSelection(index)
                    }
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
            if(it != null) {
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

    override fun onListItemClick(view : View, trainId: Int) {
        val action =
            FilterTrainFragmentDirections.actionFilterTrainFragmentToTrainDetailsFragment(trainId)
        binding.root.findNavController().navigate(action)
    }
}