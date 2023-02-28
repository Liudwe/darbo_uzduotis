package com.example.darbo_uzduotis

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.darbo_uzduotis.databinding.ActivityMainBinding
import androidx.navigation.ui.NavigationUI
import com.example.darbo_uzduotis.GlobalData
import com.example.darbo_uzduotis.R
import com.example.darbo_uzduotis.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private var isUpdating = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationBar()
        setupNavController()
    }

    private fun setupBottomNavigationBar() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_update -> { //update
                    if (isUpdating) {
                        return@setOnItemSelectedListener true
                    }
                    CoroutineScope(Dispatchers.Default).launch {
                        isUpdating = true
                        viewModel.initializeWeatherData()
                        delay(3000)
                        isUpdating = false
                    }
                    true
                }
                R.id.action_auto_update -> { //auto update
                    val sharedPreferences = applicationContext.getSharedPreferences(
                        "mySharedPreferences",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    val isAutoUpdateEnabled = sharedPreferences.getBoolean("autoUpdate", false)
                    if (isAutoUpdateEnabled) {
                        editor.putBoolean("autoUpdate", false).apply()
                    } else {
                        editor.putBoolean("autoUpdate", true).apply()
                    }
                    true
                }

                R.id.action_temperature_unit -> { //change temperature scale
                    if (isUpdating) {
                        return@setOnItemSelectedListener true
                    }
                    isUpdating = true
                    if (GlobalData.temperatureScale == "Celsius") {
                        GlobalData.temperatureScale = "Fahrenheit"
                    } else {
                        GlobalData.temperatureScale = "Celsius"
                    }

                    val menuItem =
                        binding.bottomNavigation.menu.findItem(R.id.action_temperature_unit)
                    menuItem.title = GlobalData.temperatureScale
                    viewModel.setTemperatureScale(GlobalData.temperatureScale)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(3000)
                        isUpdating = false
                    }

                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}