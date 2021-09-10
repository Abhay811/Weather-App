package com.abhay.weather.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abhay.weather.R
import com.abhay.weather.data.models.ResponseWeather
import com.abhay.weather.data.repository.remote.WeatherRepository
import com.abhay.weather.databinding.ActivityWeatherDetailsBinding
import com.abhay.weather.util.Status
import com.abhay.weather.util.unixTimestampToTimeString
import com.abhay.weather.viewmodel.MyViewModel

class WeatherDetailsActivity: AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    private lateinit var weatherRepo: WeatherRepository
    private lateinit var binding: ActivityWeatherDetailsBinding

    private var cityID:String?=null
    private var lat:String?=null
    private var lon:String?=null
    private var city:String?=null

    companion object{
        const val CITY_ID = "city_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        weatherRepo = WeatherRepository()

        binding.incInfoWeather.ivAdd.setImageResource(R.drawable.ic_arrow_back_white)
        binding.incInfoWeather.ivMore.visibility = View.GONE

        cityID = intent.getStringExtra(CITY_ID)
        viewModel.getWeatherByCityID(weatherRepo, cityID!!)
        setUpObservers()


    }
    private fun setUpObservers() {
        viewModel.weatherByCityID.observe(this, {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        binding.incInfoWeather.root.visibility = View.GONE
                        binding.progressBar.visibility=View.GONE
                        binding.animFailed.visibility=View.GONE
                        binding.animNetwork.visibility=View.GONE
                        setUpUI(it.data)
                    }
                    Status.ERROR->{
                        showFailedView(it.message)
                    }
                    Status.LOADING->{
                        binding.progressBar.visibility=View.VISIBLE
                        binding.animFailed.visibility=View.GONE
                        binding.animNetwork.visibility=View.GONE
                    }
                }
            }
        })
    }

    private fun showFailedView(message: String?) {
        binding.progressBar.visibility=View.GONE
        binding.incInfoWeather.root.visibility=View.GONE

        when(message){
            "Network Failure" -> {
                binding.animFailed.visibility=View.GONE
                binding.animNetwork.visibility=View.VISIBLE
            }
            else ->{
                binding.animNetwork.visibility=View.GONE
                binding.animFailed.visibility=View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(data: ResponseWeather?) {
        binding.incInfoWeather.tvTemp.text = data?.main?.temp.toString()
        binding.incInfoWeather.tvCityName.text = data?.name
        binding.incInfoWeather.tvWeatherCondition.text = data?.weather!![0].main
        binding.incInfoWeather.additionalInfo.tvSunriseTime.text = data.sys.sunrise.unixTimestampToTimeString()
        binding.incInfoWeather.additionalInfo.tvSunsetTime.text = data.sys.sunset.unixTimestampToTimeString()
        binding.incInfoWeather.additionalInfo.tvRealFeelText.text = "${data.main.feelsLike}${getString(R.string.degree_celsius_symbol)}"
        binding.incInfoWeather.additionalInfo.tvCloudinessText.text = "${data.clouds.all}%"
        binding.incInfoWeather.additionalInfo.tvWindSpeedText.text = "${data.wind.speed}m/s"
        binding.incInfoWeather.additionalInfo.tvHumidityText.text = "${data.main.humidity}%"
        binding.incInfoWeather.additionalInfo.tvPressureText.text = "${data.main.humidity}hPa"
        binding.incInfoWeather.additionalInfo.tvVisibilityText.text = "${data.visibility}M"

        lat = data.coord.lat.toString()
        lon = data.coord.lon.toString()
        city = data.name

    }

    fun onAddButtonClicked(view: View) {
        onBackPressed()
        finish()
    }

    fun onForecastButtonClicked(view: View) {
        startActivity(
            Intent(this@WeatherDetailsActivity,ForecastActivity::class.java)
            .putExtra(ForecastActivity.LATITUDE,lat)
            .putExtra(ForecastActivity.LONGITUDE,lon)
            .putExtra(ForecastActivity.CITY_NAME,city))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}