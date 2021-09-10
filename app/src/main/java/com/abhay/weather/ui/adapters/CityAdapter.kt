package com.abhay.weather.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.abhay.weather.data.models.Cities
import com.abhay.weather.databinding.ItemCitiesBinding
import com.abhay.weather.util.DiffUtilCallback

class CityAdapter(): RecyclerView.Adapter<CityAdapter.Holder>() {

    class Holder(binding: ItemCitiesBinding): RecyclerView.ViewHolder(binding.root) {
        val cityName = binding.tvCityName
        val countryName = binding.tvCountryName
        val addBtn = binding.ivAddCity
        val addedTV = binding.tvAdded
    }

    val differ = AsyncListDiffer(this, DiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemCitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val cities = differ.currentList[position]
        bindData(holder, cities)
    }

    private fun bindData(holder: Holder, cities: Cities?) {
        holder.apply {
            cityName.text = cities?.name
            countryName.text = cities?.country
            if (cities?.isSaved == 1) {
                addBtn.visibility = View.GONE
                addedTV.visibility = View.VISIBLE
            } else {
                addedTV.visibility = View.GONE
                addBtn.visibility = View.VISIBLE
            }
            addBtn.setOnClickListener {
                onItemClickListener?.let { it(cities!!)}
                addedTV.visibility = View.VISIBLE
                addBtn.visibility = View.GONE
            }
            itemView.setOnClickListener {
                onParentItemClickListener?.let { it(cities!!)}
            }
        }

    }
    private var onItemClickListener: ((Cities) -> Unit)? = null
    private var onParentItemClickListener: ((Cities) -> Unit)? = null

    fun setOnItemClickListener(listener: (Cities) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnParentClickListener(listener: (Cities) -> Unit) {
        onParentItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}