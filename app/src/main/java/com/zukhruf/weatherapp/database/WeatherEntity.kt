package com.zukhruf.weatherapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "weatherEntity")
data class WeatherEntity (
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "lon") var lon: Double?,
    @ColumnInfo(name = "lat") var lat: Double?,
    @ColumnInfo(name = "weather_main") var weatherMain: String?,
    @ColumnInfo(name = "weather_description") var weatherDesc: String?,
    @ColumnInfo(name = "weather_icon") var weatherIcon: String?,
    @ColumnInfo(name = "base") var base: String?,
    @ColumnInfo(name = "temperature") var temp: Double?,
    @ColumnInfo(name = "feels_like") var feelsLike: Double?,
    @ColumnInfo(name = "temp_min") var tempMin: Double?,
    @ColumnInfo(name = "temp_max") var tempMax: Double?,
    @ColumnInfo(name = "pressure") var pressure: Long?,
    @ColumnInfo(name = "humidity") var humidity: Double?,
    @ColumnInfo(name = "visibility") var visibility: Int?,
    @ColumnInfo(name = "wind_speed") var windSpeed: Double?,
    @ColumnInfo(name = "wind_deg") var windDeg: Double?,
    @ColumnInfo(name = "clouds_all") var cloudsAll: Int?,
    @ColumnInfo(name = "dt") var dt: String?,
    @ColumnInfo(name = "sys_type") var sysType: Int?,
    @ColumnInfo(name = "sys_id") var sysId: Int?,
    @ColumnInfo(name = "sys_country") var sysCountry: String?,
    @ColumnInfo(name = "sunrise") var sunrise: String?,
    @ColumnInfo(name = "sunset") var sunset: String?,
    @ColumnInfo(name = "timezone") var timezone: Int?,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "cod") var cod: Int?
) {

    @Ignore
    constructor(): this(0, 0.0, 0.0, "", "", "", "",
        0.0, 0.0, 0.0, 0.0, 0, 0.0, 0, 0.0,
        0.0, 0, "", 0, 0, "", "", "", 0, "", 0)
}