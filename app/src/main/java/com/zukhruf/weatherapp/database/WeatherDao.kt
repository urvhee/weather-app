package com.zukhruf.weatherapp.database

import androidx.room.*

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weatherEntity")
    fun get(): List<WeatherEntity>

    @Query("SELECT * FROM weatherEntity WHERE id=:id")
    fun getById(id: Int): WeatherEntity

    @Query("SELECT * FROM weatherEntity ORDER BY id DESC LIMIT 1")
    fun getLast(): WeatherEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntity: WeatherEntity)

    @Query("DELETE FROM weatherEntity WHERE id=:id")
    fun delete(id: Int)

    @Query("UPDATE weatherEntity SET lon=:lon, lat=:lat, weather_main=:weatherMain, weather_description=:weatherDescription, weather_icon=:weatherIcon, base=:base, temperature=:temp, feels_like=:feelsLike, temp_min=:tempMin, temp_max=:tempMax, pressure=:pressure, humidity=:humidity, visibility=:visibility, wind_speed=:windSpeed, wind_deg=:windDeg, clouds_all=:cloudsAll, dt=:dt, sys_type=:sysType, sys_id=:sysId, sys_country=:sysCountry, sunrise=:sunrise, sunset=:sunset, timezone=:timezone, name=:name, cod=:cod WHERE id=:id")
    fun update(id: Int, lat: Double, lon: Double, weatherMain: String, weatherDescription: String, weatherIcon: String,
               base: String, temp: Double, feelsLike: Double, tempMin: Double, tempMax: Double, pressure: Long, humidity: Double,
               visibility: Int, windSpeed: Double, windDeg: Double, cloudsAll: Int, dt: String, sysType: Int, sysId: Int,
               sysCountry: String, sunrise: String, sunset: String, timezone: Int, name: String, cod: Int)
}