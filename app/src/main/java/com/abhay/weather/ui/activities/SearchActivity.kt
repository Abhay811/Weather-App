package com.abhay.weather.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.database.DatabaseUtils
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhay.weather.R
import com.abhay.weather.data.models.Cities
import com.abhay.weather.data.models.CityUpdate
import com.abhay.weather.data.repository.local.CityRepository
import com.abhay.weather.data.repository.remote.WeatherRepository
import com.abhay.weather.databinding.ActivitySearchBinding
import com.abhay.weather.db.CityDatabase
import com.abhay.weather.ui.adapters.CityAdapter
import com.abhay.weather.util.Status
import com.abhay.weather.util.lightStatusBar
import com.abhay.weather.viewmodel.MyViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel : MyViewModel
    private lateinit var repository: CityRepository
    private lateinit var database: CityDatabase
    private lateinit var binding: ActivitySearchBinding
    private lateinit var weatherRepo: WeatherRepository
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor( R.color.white)
        lightStatusBar(this, true)
        window.navigationBarColor = resources.getColor(R.color.white)

        database = CityDatabase.getDatabase(this)
//        Log.d("Main", database.toString())
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        repository = CityRepository(database)
        weatherRepo = WeatherRepository()
//        Log.d("MAIN", repository.toString())
        binding.svSearchCity.requestFocus()
        setUpUI()
        setUpObservers()

    }

    private fun setUpObservers() {
        viewModel.cityByQuery.observe(this, {
            it ?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data!!.isNotEmpty()) {
                            binding.pbSearch.visibility = View.GONE
                            binding.rvSearchResult.visibility = View.VISIBLE
                            setUpRecyclerView(it.data)
                        } else {
                            binding.pbSearch.visibility = View.GONE
                            binding.rvSearchResult.visibility = View.GONE
                            binding.tvNoResult.visibility = View.VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        showFailedView(it.message)
                    }
                    Status.LOADING -> {
                        binding.pbSearch.visibility = View.VISIBLE
                        binding.rvSearchResult.visibility = View.GONE
                        binding.tvNoResult.visibility = View.GONE
                    }
                }
            }
        })
    }
    private fun setUpRecyclerView(data: List<Cities>) {
        val cityAdapter = CityAdapter()
        binding.rvSearchResult.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            setHasFixedSize(true)
            adapter = cityAdapter
        }
        cityAdapter.differ.submitList(data)

        cityAdapter.setOnItemClickListener { viewModel.updateSavedCities(repository, CityUpdate(it.id,1)) }
        cityAdapter.setOnParentClickListener {
            startActivity(Intent(this@SearchActivity,WeatherDetailsActivity::class.java)
                .putExtra(WeatherDetailsActivity.CITY_ID,it.id.toString()))
        }
    }
    private fun setUpUI() {
        binding.svSearchCity.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchedQuery = if (query!!.contains("'"))DatabaseUtils.sqlEscapeString(query)
                    .replace("'","") else query
//                viewModel.getCityByQuery(repository, searchedQuery)
                val d = viewModel.getWeatherByCityID(weatherRepo, searchedQuery)

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchedQuery = if (newText!!.contains("'"))DatabaseUtils.sqlEscapeString(newText)
                    .replace("'","") else newText
                viewModel.getCityByQuery(repository, searchedQuery)

                return false
            }
        })
    }

    private fun showFailedView(message: String?) {
        binding.pbSearch.visibility = View.GONE
        binding.rvSearchResult.visibility = View.GONE
        binding.tvNoResult.visibility = View.VISIBLE
        binding.tvNoResult.text = message
    }

    fun onCancelButtonClicked(view: View) {
        navigateBack()
    }

    private fun navigateBack() {
        val intent = Intent(this@SearchActivity, SavedCityActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair.create(binding.svSearchCity, getString(R.string.label_search_hint))
        )
        startActivity(intent, options.toBundle())
        Handler(Looper.myLooper()!!).postDelayed({ finish() }, 1000)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            navigateBack()
        }
        return super.onKeyDown(keyCode, event)
    }




























}