package com.abhay.weather.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhay.weather.R
import com.abhay.weather.data.models.CityUpdate
import com.abhay.weather.data.repository.local.CityRepository
import com.abhay.weather.databinding.ActivitySavedCityBinding
import com.abhay.weather.db.CityDatabase
import com.abhay.weather.ui.adapters.SavedCityAdapter
import com.abhay.weather.util.RecyclerItemTouchHelper
import com.abhay.weather.util.lightStatusBar
import com.abhay.weather.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar

class SavedCityActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private lateinit var viewModel : MyViewModel
    private lateinit var repository: CityRepository
    private lateinit var mAdapter: SavedCityAdapter
    private lateinit var binding: ActivitySavedCityBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.white)
        lightStatusBar(this, true)
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        repository = CityRepository(CityDatabase.getDatabase(this))
        mAdapter = SavedCityAdapter()

        setUpRecyclerView()
        setUpObservers()

    }
    private fun setUpObservers() {
        viewModel.getSavedCities(repository, 1).observe(this, { cities ->
            mAdapter.differ.submitList(cities)
        })
    }
    private fun setUpRecyclerView() {
        binding.rvSavedCity.apply {
            layoutManager = LinearLayoutManager(this@SavedCityActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
        ItemTouchHelper(RecyclerItemTouchHelper(this@SavedCityActivity)).attachToRecyclerView(binding.rvSavedCity)

        mAdapter.setOnItemClickListener {
            startActivity(Intent(this@SavedCityActivity, WeatherDetailsActivity::class.java)
                .putExtra(WeatherDetailsActivity.CITY_ID, it.id.toString()))
        }
    }

    fun onSearchTextClicked(view: View) {
        val intent = Intent(this@SavedCityActivity, SearchActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this, Pair.create(binding.tvCitySearch, getString(R.string.label_search_hint)))

        startActivity(intent, options.toBundle())
        Handler(Looper.myLooper()!!).postDelayed({ finish() }, 1000)
    }

    fun onBackButtonClicked(view: View) {
        onBackPressed()
        finish()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is SavedCityAdapter.Holder) {
            val pos = viewHolder.adapterPosition
            val cities = mAdapter.differ.currentList[pos]
            viewModel.updateSavedCities(CityRepository(CityDatabase.getDatabase(this@SavedCityActivity)),
                CityUpdate(cities.id,0)
            )

            Snackbar.make(binding.clParent,"City removed from saved items", Snackbar.LENGTH_LONG).apply {
                setAction("Undo"){
                    viewModel.updateSavedCities(CityRepository(CityDatabase.getDatabase(this@SavedCityActivity)),
                        CityUpdate(cities.id,1)
                    )
                }
                setBackgroundTint(resources.getColor(R.color.colorPrimary))
                setActionTextColor(resources.getColor(R.color.color_grey))
                show()
            }

        }

    }

}