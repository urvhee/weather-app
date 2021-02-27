package com.zukhruf.weatherapp

import com.zukhruf.weatherapp.database.WeatherEntity
import com.zukhruf.weatherapp.model.ResponseWeather

interface MainView {
    fun onSuccess(data: ResponseWeather?)
    fun onError(msg: String)
    fun onShowLoading()
    fun onHideLoading()

    fun onSuccessCache(data: WeatherEntity)
}