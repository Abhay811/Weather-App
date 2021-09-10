package com.abhay.weather.data.repository.local

import com.abhay.weather.data.models.Cities
import com.abhay.weather.data.models.CityUpdate
import com.abhay.weather.db.CityDatabase

class CityRepository (private val database: CityDatabase) {

    suspend fun searchCities(key: String) = database.getCity().searchCity(key)
    suspend fun updateSavedCities(obj : CityUpdate) = database.getCity().updateSavedCity(obj)
    fun getSavedCities(key: Int) = database.getCity().getSavedCity(key)
    suspend fun deleteSavedCities(cities: Cities) = database.getCity().deleteSavedCity(cities)

}