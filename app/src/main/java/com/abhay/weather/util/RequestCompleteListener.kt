package com.abhay.weather.util

interface RequestCompleteListener<T> {
    fun onRequestCompleted(data : T)
    fun onRequestFailed(errorMessage: String?)
}