package com.abhay.weather.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abhay.weather.data.models.Cities
import com.abhay.weather.data.models.CityUpdate


@Dao
interface CityDao {

    @Query("SELECT * FROM city_bd WHERE name LIKE :key || '%'")
    suspend fun searchCity(key:String):List<Cities>

    @Update(entity = Cities::class)
    suspend fun updateSavedCity(vararg obj:CityUpdate):Int

    @Query("SELECT * FROM city_bd WHERE isSaved= :key")
    fun getSavedCity(key:Int):LiveData<List<Cities>>

    @Delete
    suspend fun deleteSavedCity(city:Cities)
}