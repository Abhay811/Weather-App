package com.abhay.weather.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.abhay.weather.R
import com.abhay.weather.data.models.Daily
import com.abhay.weather.databinding.ItemForecastBinding
import com.abhay.weather.util.DiffUtilCallbackForecast
import com.abhay.weather.util.unixTimestampToDateTimeString
import com.abhay.weather.util.unixTimestampToTimeString
import com.bumptech.glide.Glide
import android.content.Context

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.Holder>() {
    val differ = AsyncListDiffer(this, DiffUtilCallbackForecast())

    var context : Context? = null

    class Holder(
        binding: ItemForecastBinding,
    ): RecyclerView.ViewHolder(binding.root) {
        val timeForecast = binding.tvTimeForecast
        val weatherCondition = binding.tvWeatherCondition
        val dayTemp = binding.cvDayForecast.tvDayTemp
        val eveTemp = binding.cvDayForecast.tvEveTemp
        val nightTemp = binding.cvDayForecast.tvNightTemp
        val maxTemp = binding.cvDayForecast.tvMaxTemp
        val minTemp = binding.cvDayForecast.tvMinTemp
        val mornFeel = binding.cvDayForecast.tvMornFeel
        val dayFeel = binding.cvDayForecast.tvDayFeel
        val eveFeel = binding.cvDayForecast.tvEveFeel
        val nightFeel = binding.cvDayForecast.tvNightFeel
        val sunriseTime = binding.cvDayForecast.tvSunriseTime
        val sunsetTime = binding.cvDayForecast.tvSunsetTime
        val weatherIcon = binding.ivWeatherIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = differ.currentList[position]
        bindData(data, holder)
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(data: Daily?, holder: Holder) {
        val weatherConditionIconUrl = "http://openweathermap.org/img/w/${data!!.weather[0].icon}.png"
        holder.apply {
            timeForecast.text = data.dt.unixTimestampToDateTimeString()
            if(!(context as Activity).isFinishing) Glide.with(context as Activity).load(weatherConditionIconUrl).into(weatherIcon)

            weatherCondition.text = data.weather[0].main
            dayTemp.text = "Day\n${data.temp.day}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            eveTemp.text = "Evening\n${data.temp.eve}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            nightTemp.text = "Night\n${data.temp.night}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            maxTemp.text = "Max\n${data.temp.max}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            minTemp.text = "Min\n${data.temp.min}${(context as Activity).getString(R.string.degree_celsius_symbol)}"

            mornFeel.text = "Morning\n${data.feelsLike.morn}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            dayFeel.text = "Day\n${data.feelsLike.day}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            eveFeel.text = "Evening\n${data.feelsLike.eve}${(context as Activity).getString(R.string.degree_celsius_symbol)}"
            nightFeel.text = "Night\n${data.feelsLike.night}${(context as Activity).getString(R.string.degree_celsius_symbol)}"

            sunriseTime.text = data.sunrise.unixTimestampToTimeString()
            sunsetTime.text = data.sunset.unixTimestampToTimeString()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}