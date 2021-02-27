package com.zukhruf.weatherapp.network

import com.zukhruf.weatherapp.model.ResponseWeather
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather")
    fun getWeather(
        @Query("lat") lat: String = "",
        @Query("lon") lon: String = "",
        @Query("appid") appId: String = "",
        @Query("units") units: String = "metric"
    ): Observable<ResponseWeather>

}