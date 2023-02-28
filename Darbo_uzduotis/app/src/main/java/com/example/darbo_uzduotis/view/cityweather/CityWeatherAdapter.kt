package com.example.darbo_uzduotis.view.cityweather

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.darbo_uzduotis.data.CityWeatherDetailsItem
import com.example.darbo_uzduotis.databinding.AdapterCityWeatherDetailsItemBinding


class CityWeatherAdapter(
    private val context: Context,
    private var cityWeatherList: List<CityWeatherDetailsItem> = emptyList(),
    private var listener: OnItemClickListener? = null
) : RecyclerView.Adapter<CityWeatherAdapter.ViewHolder>() {
    inner class ViewHolder(bind: AdapterCityWeatherDetailsItemBinding) :
        RecyclerView.ViewHolder(bind.root) {
        var binding: AdapterCityWeatherDetailsItemBinding = bind
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(AdapterCityWeatherDetailsItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cityWeatherList[position]
        with(holder.binding) {
            titleTextView.text = item.city
            temperatureTextView.text = item.temperature
            titleTextView.setOnClickListener {
                val action =
                    CityWeatherFragmentDirections.actionCityWeatherFragmentToCityWeekFragment(item.city)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return cityWeatherList.size
    }

    fun updateList(
        items: List<CityWeatherDetailsItem>
    ) {
        cityWeatherList = items
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(cityName: String)
    }
}