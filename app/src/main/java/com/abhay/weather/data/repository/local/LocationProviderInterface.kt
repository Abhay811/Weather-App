package com.abhay.weather.data.repository.local

import com.abhay.weather.data.models.LocationData
import com.abhay.weather.util.RequestCompleteListener

interface LocationProviderInterface {
    fun getUserCurrentLocation(callback: RequestCompleteListener<LocationData>)
}