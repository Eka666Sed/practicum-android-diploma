package ru.practicum.android.diploma.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentMainBinding
import ru.practicum.android.diploma.domain.models.ResponseStatus
import ru.practicum.android.diploma.domain.models.VacancySearchResult
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.presentation.main.MainViewModel
import ru.practicum.android.diploma.ui.main.vacancy.VacancyAdapter

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null
    private var editTextValue = ""
    private lateinit var vacancies: ArrayList<Vacancy>
    private lateinit var adapter: VacancyAdapter

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vacancies = ArrayList<Vacancy>()
        adapter = VacancyAdapter(vacancies)
        createTextWatcher()

        adapter.itemClickListener = { vacancy ->
            if (viewModel.clickDebounce()) {
                startJobVacancyFragment(vacancy.vacancyId)
            }
        }

        binding!!.rvVacancyList.adapter = adapter
        binding!!.ivSearch.setOnClickListener {
            binding!!.etSearch.setText("")
            breakSearch()
        }

        viewModel.foundVacancies.observe(viewLifecycleOwner) {
            processingSearchStatus(it)
        }
    }

    override fun onDestroyView() {
        binding = null
        viewModel.onDestroy()
        super.onDestroyView()
    }

    private fun startJobVacancyFragment(vacancyId: String) {
        TODO()
    }

    private fun startSearch() {
        changeViewsVisibility(true)
        viewModel.changeRequestText(binding!!.etSearch.text.toString())
        viewModel.searchDebounce()
    }

    private fun changeViewsVisibility(action: Boolean) {
        binding!!.ivSearchPlaceholder.isVisible = !action
    }

    private fun createTextWatcher() {
        binding!!.etSearch.doOnTextChanged{ s: CharSequence?, start: Int, count: Int, after: Int ->
            editTextValue = binding!!.etSearch.text.toString()
            if (editTextValue.isEmpty()) {
                binding!!.ivSearch.setImageResource(R.drawable.ic_search)
                changeViewsVisibility(false)
                breakSearch()
            } else {
                binding!!.ivSearch.setImageResource(R.drawable.ic_clear)
                startSearch()
            }
        }
    }

    private fun processingSearchStatus(vacancySearchResult: VacancySearchResult) {
        vacancies.clear()
        binding!!.rvVacancyList.isVisible = false
        binding!!.ivSearchPlaceholder.isVisible = false
        binding!!.pbSearch.isVisible = false
        binding!!.chip.isVisible = false
        when (vacancySearchResult.responseStatus) {
            ResponseStatus.OK -> {
                showOkStatus(vacancySearchResult.results, vacancySearchResult.found)
            }

            ResponseStatus.LOADING -> {
                showLoadingStatus()
            }

            ResponseStatus.DEFAULT -> {
                showDefaultStatus()
            }

            ResponseStatus.BAD -> {
            }

            ResponseStatus.NO_CONNECTION -> {
            }
        }
    }

    private fun showLoadingStatus() {
        binding!!.pbSearch.isVisible = true
    }

    private fun showOkStatus(listVacancies: List<Vacancy>, vacanciesFound: Int) {
        vacancies.addAll(listVacancies)
        adapter.notifyDataSetChanged()
        binding!!.chip.text =
            requireContext().getString(R.string.found) + " " + vacanciesFound.toString() + " " + requireContext().resources.getQuantityString(
                R.plurals.vacancy,
                vacanciesFound
            )
        binding!!.rvVacancyList.isVisible = true
        binding!!.chip.isVisible = true
    }

    private fun showDefaultStatus() {
        binding!!.ivSearchPlaceholder.isVisible = true
    }

    private fun breakSearch() {
        viewModel.onDestroy()
        binding!!.ivSearchPlaceholder.isVisible = true
        binding!!.pbSearch.isVisible = false
        binding!!.chip.isVisible = false
        binding!!.rvVacancyList.isVisible = false
    }
}
