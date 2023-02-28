package com.example.darbo_uzduotis.view.cityday_week

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.darbo_uzduotis.data.WeekDaysDetailsItem
import com.example.darbo_uzduotis.databinding.AdapterWeatherWeekDetailsItemBinding


class WeekDaysAdapter(
    private val context: Context,
    private var cityWeekrList: List<WeekDaysDetailsItem> = emptyList(),
    private var listener: OnItemClickListener? = null
) : RecyclerView.Adapter<WeekDaysAdapter.ViewHolder>() {
    inner class ViewHolder(bind: AdapterWeatherWeekDetailsItemBinding) :
        RecyclerView.ViewHolder(bind.root) {
        var binding: AdapterWeatherWeekDetailsItemBinding = bind
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(AdapterWeatherWeekDetailsItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cityWeekrList[position]
        with(holder.binding) {
            dayTextView.text = item.day
            dayTemperatureTextView.text = item.day_temperature
            if(!item.night_temperature.isNullOrEmpty()){
                nightTemperatureTextView.visibility = View.VISIBLE
                nightTemperatureTextView.text = item.night_temperature
            }
            else {
                nightTemperatureTextView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return cityWeekrList.size
    }

    fun updateList(
        items: List<WeekDaysDetailsItem>
    ) {
        cityWeekrList = items
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(cityName: String)
    }

}