package com.abhay.weather.network

import android.util.Log
import com.abhay.weather.network.logging.Level
import com.abhay.weather.network.logging.LoggingInterceptor
import com.abhay.weather.util.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private val retrofit by lazy {
            val httpClient =
                OkHttpClient.Builder().addInterceptor(QueryParameterAddInterceptor()).apply {
                    addInterceptor(
                        LoggingInterceptor.Builder()
                            .setLevel(Level.BASIC)
                            .log(Platform.INFO)
                            .build()
                    )
                }.build()
            Log.d("Main", "alpha ${httpClient.toString()}")

            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()

        }
        val api by lazy {
            retrofit.create(ApiInterface::class.java)
        }
    }
}