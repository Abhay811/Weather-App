package com.abhay.weather.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhay.weather.R
import com.abhay.weather.data.repository.remote.WeatherRepository
import com.abhay.weather.databinding.ActivityForecastBinding
import com.abhay.weather.ui.adapters.ForecastAdapter
import com.abhay.weather.util.Status
import com.abhay.weather.util.lightStatusBar
import com.abhay.weather.viewmodel.MyViewModel

class ForecastActivity: AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var mAdapter: ForecastAdapter
    private lateinit var binding: ActivityForecastBinding
    private var lat:String?=null
    private var lon:String?=null
    private var city:String?=null



    companion object {
        const val LATITUDE = "lat"
        const val LONGITUDE = "lon"
        const val CITY_NAME = "city"
        const val EXCLUDE = "current,minutely,hourly"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.white)
        lightStatusBar(this, true)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        repository = WeatherRepository()
        mAdapter = ForecastAdapter()

        lat = intent.getStringExtra(LATITUDE)
        lon = intent.getStringExtra(LONGITUDE)
        city = intent.getStringExtra(CITY_NAME)

        binding.toolLayout.tvToolTitle.text = city

        if (lat!=null && lon!=null) viewModel.getWeatherForecast(repository,lat!!,lon!!,EXCLUDE)

        setUpRecyclerView()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.weatherForecast.observe(this,{
            it?.let {resource ->
                when(resource.status) {
                    Status.SUCCESS-> {
                        binding.progressBar.visibility=View.GONE
                        binding.tvErrorMsg.visibility=View.GONE
                        binding.animFailed.visibility=View.GONE
                        binding.animNetwork.visibility=View.GONE
                        binding.rvForecast.visibility=View.VISIBLE
                        mAdapter.differ.submitList(it.data?.daily)
                    }
                    Status.ERROR-> {
                        showFailedView(it.message)
                    }
                    Status.LOADING-> {
                        binding.progressBar.visibility=View.VISIBLE
                        binding.tvErrorMsg.visibility=View.GONE
                        binding.rvForecast.visibility=View.GONE
                        binding.animFailed.visibility=View.GONE
                        binding.animNetwork.visibility=View.GONE
                    }
                }
            }
        })
    }

    private fun showFailedView(message: String?) {
        binding.progressBar.visibility=View.GONE
        binding.tvErrorMsg.visibility=View.GONE
        binding.rvForecast.visibility=View.GONE
        when(message){
            "Network Failure" -> {
                binding.animFailed.visibility=View.GONE
                binding.animNetwork.visibility=View.VISIBLE
            }
            else ->{
                binding.animNetwork.visibility=View.GONE
                binding.animFailed.visibility= View.VISIBLE
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(this@ForecastActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    fun onBackButtonClicked(view: View) {
        onBackPressed()
        finish()
    }
}