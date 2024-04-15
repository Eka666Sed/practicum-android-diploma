package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilteringSettingsBinding
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.presentation.filter.FilteringSettingsViewModel

class FilteringSettingsFragment : Fragment() {

    private var binding: FragmentFilteringSettingsBinding? = null

    private val viewModel by viewModel<FilteringSettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilteringSettingsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createFirstHalfClickListeners()
        createSecondHalfClickListeners()
        createTextWatcher()
        viewModel.onCreate()

        //insertFilterData(viewModel.liveData.value!!)
        viewModel.liveData.observe(viewLifecycleOwner) {
            insertFilterData(it)
        }

        // нужен слушатеть лайф даты с классом фильтра
        // нужен метод который из лайфдаты выставит все данные
        // кнопка применить - ее видимость определяется отдельным методом при сравнении с
        // последним сохраненным фильтром
        binding!!.tietSalary.setOnFocusChangeListener { _, b ->
            if (b) {
                binding!!.tilSalaryLayout.defaultHintTextColor = resources.getColorStateList(R.color.blue)
            } else if (binding!!.tietSalary.text!!.isNotEmpty()) {
                binding!!.tilSalaryLayout.defaultHintTextColor = resources.getColorStateList(R.color.black)
            } else {
                binding!!.tilSalaryLayout.defaultHintTextColor = resources.getColorStateList(R.color.gray_white)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkTIETContent()
        installButtonResetVisibility()
    }

    override fun onDestroyView() {
        viewModel.putFilterInSharedPrefs()
        viewModel.onDestroy()
        binding = null
        super.onDestroyView()
    }

    private fun createFirstHalfClickListeners() {
        binding!!.ivBack.setOnClickListener {
            viewModel.putStarSearchStatusInSharedPrefs(false)
            findNavController().navigateUp()
        }
        binding!!.ivSalaryClear.setOnClickListener {
            binding!!.tietSalary.text!!.clear()
            binding!!.tilSalaryLayout.defaultHintTextColor = resources.getColorStateList(R.color.gray_white)
            makeCurrentFilter()
            installButtonResetVisibility()
        }
        binding!!.bReset.setOnClickListener {
            binding!!.tietIndustry.text!!.clear()
            binding!!.tietJobPlace.text!!.clear()
            binding!!.tietSalary.text!!.clear()
            binding!!.cbNoSalary.isChecked = false
            binding!!.bReset.isVisible = false
            makeCurrentFilter()
            checkTIETContent()
        }
        binding!!.cbNoSalary.setOnClickListener {
            makeCurrentFilter()
            installButtonResetVisibility()
        }
        binding!!.bApply.setOnClickListener {
            viewModel.putStarSearchStatusInSharedPrefs(true)
            findNavController().navigateUp()
        }


    }

    private fun createSecondHalfClickListeners() {
        binding!!.ivJobPlaceClear.setOnClickListener {
            binding!!.tietJobPlace.text!!.clear()
            installButtonResetVisibility()
            makeCurrentFilter()
            checkTIETContent()
        }
        binding!!.ivIndustryClear.setOnClickListener {
            binding!!.tietIndustry.text!!.clear()
            installButtonResetVisibility()
            makeCurrentFilter()
            checkTIETContent()
        }
        binding!!.ivArrowRightJobPlace.setOnClickListener {
            findNavController().navigate(
                FilteringSettingsFragmentDirections.actionFilteringSettingsFragmentToPlacesOfWorkFragment()
            )
        }
        binding!!.ivArrowRightIndustry.setOnClickListener {
            findNavController().navigate(
                FilteringSettingsFragmentDirections.actionFilteringSettingsFragmentToIndustrySelectionFragment()
            )
        }
    }

    private fun createTextWatcher() {
        (binding!!.tietSalary as EditText).doOnTextChanged { p0: CharSequence?, start: Int, before: Int, count: Int ->
            binding!!.ivSalaryClear.isVisible = !p0.isNullOrEmpty()
            if (binding!!.tietSalary.isFocused) {
                makeCurrentFilter()
            }
            installButtonResetVisibility()
        }
    }

    private fun installButtonResetVisibility() {
        val buttonsVisibility = binding!!.tietIndustry.text!!.isNotEmpty()
            || binding!!.tietJobPlace.text!!.isNotEmpty()
            || binding!!.tietSalary.text!!.isNotEmpty()
            || binding!!.cbNoSalary.isChecked

        binding!!.bReset.isVisible = buttonsVisibility
        binding!!.bApply.isVisible = viewModel.compareFilters()
    }

    private fun checkTIETContent() {
        if (binding!!.tietJobPlace.text!!.isNotEmpty()) {
            binding!!.ivJobPlaceClear.isVisible = true
            binding!!.ivArrowRightJobPlace.isVisible = false
        } else {
            binding!!.ivJobPlaceClear.isVisible = false
            binding!!.ivArrowRightJobPlace.isVisible = true
        }
        if (binding!!.tietIndustry.text!!.isNotEmpty()) {
            binding!!.ivIndustryClear.isVisible = true
            binding!!.ivArrowRightIndustry.isVisible = false
        } else {
            binding!!.ivIndustryClear.isVisible = false
            binding!!.ivArrowRightIndustry.isVisible = true
        }
    }

    private fun insertFilterData(filter: Filters) {
        if (filter.countryName.isNotEmpty()) {
            (binding!!.tietJobPlace as TextView).text = filter.countryName
        }
        if (filter.regionName.isNotEmpty()) {
            (binding!!.tietJobPlace as TextView).text = filter.countryName + ", " + filter.regionName
        }
        if (filter.industryName.isNotEmpty()) {
            (binding!!.tietIndustry as TextView).text = filter.industryName
        }
        if (filter.salary != 0) {
            (binding!!.tietSalary as TextView).text = filter.salary.toString()
        }
        binding!!.cbNoSalary.isChecked = filter.doNotShowWithoutSalarySetting
    }

    private fun makeCurrentFilter() {
        viewModel.makeCurrentFilter(
            binding!!.tietJobPlace.text!!.isEmpty(),
            binding!!.tietIndustry.text!!.isEmpty(),
            binding!!.tietSalary.text.toString(),
            binding!!.cbNoSalary.isChecked
        )
    }
}
