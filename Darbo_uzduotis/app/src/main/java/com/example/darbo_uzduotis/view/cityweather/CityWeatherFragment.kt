package com.example.darbo_uzduotis.view.cityweather

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.darbo_uzduotis.GlobalData
import com.example.darbo_uzduotis.R
import com.example.darbo_uzduotis.databinding.FragmentCityWeatherBinding
import com.example.darbo_uzduotis.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.unlokk.onboarding.setDivider
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CityWeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var binding: FragmentCityWeatherBinding
    private lateinit var adapter: CityWeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityWeatherBinding.inflate(inflater, container, false)
        adapter = CityWeatherAdapter(context = requireContext())
        setupRecyclerView()
        requireActivity().title = "Weather application"
        viewModel.setTemperatureScale(GlobalData.temperatureScale)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        Handler(Looper.getMainLooper()).postDelayed({
            CoroutineScope(Dispatchers.Default).launch {
                if (viewModel.isDatabaseEmpty() != 0) {
                    Log.d("Database", "is initialized")
                } else {
                    viewModel.initializeWeatherData()
                    Log.d("Database", "is empty")
                }

            }
        }, 2000) // delay for 2 seconds
        observeChanges(currentDate)
        viewModel.autoUpdate()
    }

    private fun observeChanges(currentDate: String) {
        viewModel.temperatureScale.observe(viewLifecycleOwner) { globalData ->
            viewModel.getCityWeather(currentDate, GlobalData.temperatureScale)
                .observe(viewLifecycleOwner) {
                    adapter.updateList(it)
                }
        }
    }

    private fun setupRecyclerView() {
        binding.cityWeatherRecyclerView.setDivider(R.drawable.recycler_view_divider)
        binding.cityWeatherRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cityWeatherRecyclerView.adapter = adapter
        binding.cityWeatherRecyclerView.setHasFixedSize(true)
    }

}