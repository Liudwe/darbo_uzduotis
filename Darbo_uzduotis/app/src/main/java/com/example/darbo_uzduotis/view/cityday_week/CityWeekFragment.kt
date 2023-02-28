package com.example.darbo_uzduotis.view.cityday_week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.darbo_uzduotis.GlobalData
import com.example.darbo_uzduotis.R
import com.example.darbo_uzduotis.databinding.FragmentCityWeekBinding
import com.example.darbo_uzduotis.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.unlokk.onboarding.setDivider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class CityWeekFragment : Fragment() {
    private lateinit var cityName: String
    val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var binding: FragmentCityWeekBinding
    private lateinit var adapter: WeekDaysAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityWeekBinding.inflate(inflater, container, false)
        adapter = WeekDaysAdapter(context = requireContext())
        setupRecyclerView()
        arguments?.let {
            cityName = it.getString("city") ?: ""
        }
        viewModel.setTemperatureScale(GlobalData.temperatureScale)
        requireActivity().title = cityName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val startingDate: LocalDate = LocalDate.now()
        val endingDate: LocalDate = LocalDate.now()
        val dateFormatStart =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00", Locale.getDefault())
        val dateFormatEnd =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'23:00", Locale.getDefault())
        val startDate = dateFormatStart.format(startingDate)
        val endDate = dateFormatEnd.format(endingDate)
        loadDayTemperature(startDate, endDate)
        loadWeekTemperature()
        observeChanges(startDate, endDate)
    }

    private fun observeChanges(startDate: String, endDate: String) {
        viewModel.temperatureScale.observe(viewLifecycleOwner) { globalData ->
            cityName.let { it ->
                if (globalData != null) {
                    viewModel.getCityDayWeather(it, startDate, endDate, globalData)
                        .observe(viewLifecycleOwner) {
                            adapter.updateList(it)
                        }
                }
            }
        }
    }

    private fun loadDayTemperature(startDate: String, endDate: String) {
        binding.dayTemperature.setOnClickListener {
            cityName.let { it ->
                viewModel.getCityDayWeather(it, startDate, endDate, GlobalData.temperatureScale)
                    .observe(viewLifecycleOwner) {
                        adapter.updateList(it)
                    }

            }
        }
    }

    private fun loadWeekTemperature() {
        binding.weekTemperature.setOnClickListener {
            val startingDate: LocalDate = LocalDate.now().minusDays(1)
            val endingDate: LocalDate = LocalDate.now().plusDays(7)
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00", Locale.getDefault())
            val startDate = dateFormat.format(startingDate)
            val endDate = dateFormat.format(endingDate)
            cityName.let { it ->
                viewModel.getCityWeekWeather(it, startDate, endDate, GlobalData.temperatureScale)
                    .observe(viewLifecycleOwner) {
                        adapter.updateList(it)
                    }
            }
        }
    }


    private fun setupRecyclerView() {
        binding.cityWeekRecyclerView.setDivider(R.drawable.recycler_view_divider)
        binding.cityWeekRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.cityWeekRecyclerView.adapter = adapter
        binding.cityWeekRecyclerView.setHasFixedSize(true)
    }
}