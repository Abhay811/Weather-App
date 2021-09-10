package com.abhay.weather.network


import android.util.Log
import com.abhay.weather.Weather
import com.abhay.weather.util.APP_ID
import com.abhay.weather.util.PrefManager
import okhttp3.Interceptor
import okhttp3.Response

class QueryParameterAddInterceptor : Interceptor {
    val context = Weather.context
    private val prefManager = PrefManager(context)



    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("appid", APP_ID)
            .addQueryParameter("units", prefManager.tempUnit)
            .build()
//        Log.d("Main", "beta ${url.toString()}")
        val request = chain.request().newBuilder()
            .url(url)
            .build()
//        Log.d("Main", "beta2${request.toString()}")

        return chain.proceed(request)
    }
}