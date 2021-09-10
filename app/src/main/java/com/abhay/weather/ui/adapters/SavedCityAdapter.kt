package com.abhay.weather.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.abhay.weather.data.models.Cities
import com.abhay.weather.databinding.ItemSavedCityBinding
import com.abhay.weather.util.DiffUtilCallback

class SavedCityAdapter: RecyclerView.Adapter<SavedCityAdapter.Holder>() {
    val differ = AsyncListDiffer(this, DiffUtilCallback())

    private var onItemClickListener : ((Cities) -> Unit)? = null
    fun setOnItemClickListener(listener: (Cities)->Unit){
        onItemClickListener = listener
    }

    class Holder(binding: ItemSavedCityBinding): RecyclerView.ViewHolder(binding.root) {
        val cityName = binding.tvCityNameSearch
        val countryName = binding.tvCountryNameSearch
        val temperature = binding.tvCityTemp
        val foregroundView = binding.viewForeground
        val backgroundView = binding.viewBackground
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemSavedCityBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val cities = differ.currentList[position]
        bindData(cities, holder)
    }

    private fun bindData(cities: Cities?, holder: Holder) {
        holder.apply {
            cityName.text = cities?.name
            countryName.text = cities?.country
            temperature.text = ""
            itemView.setOnClickListener { onItemClickListener?.let { it(cities!!)}}
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}