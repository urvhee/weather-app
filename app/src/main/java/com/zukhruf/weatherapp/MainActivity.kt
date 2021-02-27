package com.zukhruf.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.zukhruf.weatherapp.database.WeatherEntity
import com.zukhruf.weatherapp.model.*
import com.zukhruf.weatherapp.utils.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.address
import kotlinx.android.synthetic.main.activity_main.humidity
import kotlinx.android.synthetic.main.activity_main.loader
import kotlinx.android.synthetic.main.activity_main.pressure
import kotlinx.android.synthetic.main.activity_main.status
import kotlinx.android.synthetic.main.activity_main.sunrise
import kotlinx.android.synthetic.main.activity_main.sunset
import kotlinx.android.synthetic.main.activity_main.temp
import kotlinx.android.synthetic.main.activity_main.temp_max
import kotlinx.android.synthetic.main.activity_main.temp_min
import kotlinx.android.synthetic.main.activity_main.updated_at
import kotlinx.android.synthetic.main.activity_main.wind
import org.jetbrains.anko.toast
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), MainView {

    private var presenter: MainPresenter? = null
    private var weatherData: ResponseWeather? = null
    private var weatherModel: WeatherEntity? = null

    // responseWeather jsons
    private var clouds: Clouds? = null
    private var coord: Coord? = null
    private var main: Main? = null
    private var sys: Sys? = null
    private var weatherItem: List<WeatherItem?>? = null
    private var windData: Wind? = null

    private var addressData = ""
    private var dt: Long = 0
    private var updatedAtData = ""
    private var weatherDescriptionData = ""
    private var tempData = ""
    private var tempMinData = ""
    private var tempMaxData = ""
    private var sunriseData: Long = 0
    private var sunsetData: Long = 0
    private var pressureData = 0
    private var humidityData = 0.0
    private var windSpeedData = 0.0

    private var lat = ""
    private var lon = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(DebugTree())

        loader_container.visibility = View.VISIBLE
        overviewContainer.visibility = View.GONE
        addressContainer.visibility = View.GONE
        detailsContainer.visibility = View.GONE

        initPresenter()
        setUpLocationListener()

        Handler(Looper.getMainLooper()).postDelayed({
            presenter?.getWeather(lat, lon, appId)
        }, 1500)

        sr_container.setOnRefreshListener {
            sr_container.isRefreshing = true
            presenter?.getWeather(lat, lon, appId)
        }
    }

    private fun initPresenter() {
        presenter = MainPresenter(this, this)
    }

    private fun clearView() {
        address.text = ""
        updated_at.text = ""
        status.text = ""
        temp.text = ""
        temp_min.text = ""
        temp_max.text = ""
        sunrise.text = ""
        sunset.text = ""
        wind.text = ""
        pressure.text = ""
        humidity.text = ""
    }

    private fun clearData() {
        addressData = ""
        dt = 0
        updatedAtData = ""
        weatherDescriptionData = ""
        tempData = ""
        tempMinData = ""
        tempMaxData = ""
        sunriseData = 0
        sunsetData = 0
        pressureData = 0
        humidityData = 0.0
        windSpeedData = 0.0
    }

    private fun implementView() {
        address.text = addressData
        updated_at.text = updatedAtData
        status.text = weatherDescriptionData!!.capitalize()
        temp.text = tempData
        temp_min.text = "Min temp: $tempMinData"
        temp_max.text = "Max temp: $tempMaxData"
        sunrise.text = "${SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunriseData*1000))}"
        sunset.text = "${SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunsetData*1000))}"
        wind.text = windSpeedData.toString()
        pressure.text = "$pressureData hPa"
        humidity.text = "$humidityData%"
    }

    private fun assignData() {
        val model = WeatherEntity()

        model.id = null

        model.lon = coord?.lon ?: 0.0
        model.lat = coord?.lat ?: 0.0
        model.weatherMain = weatherItem?.get(0)?.main ?: ""
        model.weatherDesc = weatherItem?.get(0)?.description ?: ""
        model.weatherIcon = weatherItem?.get(0)?.icon ?: ""
        model.base = weatherData?.base ?: ""
        model.temp = main?.temp ?: 0.0
        model.feelsLike = main?.feelsLike ?: 0.0
        model.tempMin = main?.tempMin ?: 0.0
        model.tempMax = main?.tempMax ?: 0.0
        model.pressure = main?.pressure?.toLong() ?: 0
        model.humidity = main?.humidity?.toDouble() ?: 0.0
        model.visibility = weatherData?.visibility ?: 0
        model.windSpeed = windData?.speed ?: 0.0
        model.windDeg = windData?.deg?.toDouble() ?: 0.0
        model.cloudsAll = clouds?.all ?: 0
        model.dt = weatherData?.dt.toString() ?: ""
        model.sysType = sys?.type ?: 0
        model.sysId = sys?.id ?: 0
        model.sysCountry = sys?.country ?: ""
        model.sunrise = sys?.sunrise.toString()
        model.sunset = sys?.sunset.toString()
        model.timezone = weatherData?.timezone ?: 0
        model.name = weatherData?.name ?: ""
        model.cod = weatherData?.cod ?: 0

        Timber.v(model.toString())
        presenter?.saveCache(model!!)
    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        lat = location.latitude.toString()
                        lon = location.longitude.toString()
                    }
                    Timber.e("lat is $lat\n lon is $lon")
                }
            },
            Looper.myLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Location Permission is not granted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccess(data: ResponseWeather?) {
        weatherData = data
        clouds = weatherData?.clouds
        coord = weatherData?.coord
        main = weatherData?.main
        sys = weatherData?.sys
        weatherItem = weatherData?.weather
        windData = weatherData?.wind

        clearData()

        addressData = "${weatherData?.name}, ${sys?.country}"
        dt = weatherData?.dt?.toLong() ?: 0
        updatedAtData = "Updated at: ${SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(dt*1000))}"
        weatherDescriptionData = weatherItem?.get(0)?.description!!
        tempData = "${main?.temp?.toInt()}°C"
        tempMinData = "${main?.tempMin}°C"
        tempMaxData = "${main?.tempMax}°C"
        sunriseData = sys?.sunrise!!.toLong()
        sunsetData = sys?.sunset!!.toLong()
        pressureData = main?.pressure!!
        humidityData = main?.humidity!!.toDouble()
        windSpeedData = windData?.speed!!

        implementView()
        assignData()

        sr_container.isRefreshing = false
    }

    override fun onError(msg: String) {
        Timber.e("Error is $msg")

        sr_container.isRefreshing = false

        presenter?.getLast()
        if (weatherModel?.name.isNullOrEmpty()) {
            toast("We're encountering an error")
            presenter?.getWeather(lat, lon, appId)
        } else {
            toast("Cannot retrieve latest data at the moment\nSwipe down to refresh or check your internet access")
        }
    }

    override fun onShowLoading() {
        clearView()
        loader_container.visibility = View.VISIBLE
        overviewContainer.visibility = View.GONE
        addressContainer.visibility = View.GONE
        detailsContainer.visibility = View.GONE
    }

    override fun onHideLoading() {
        loader_container.visibility = View.GONE
        overviewContainer.visibility = View.VISIBLE
        addressContainer.visibility = View.VISIBLE
        detailsContainer.visibility = View.VISIBLE
    }

    override fun onSuccessCache(data: WeatherEntity) {
        clearData()
        weatherModel = data

        addressData = "${data.name}, ${data.sysCountry}"
        dt = data.dt!!.toLong()
        updatedAtData = "Updated at: ${SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(dt*1000))}"
        weatherDescriptionData = data.weatherDesc!!
        tempData = "${data.temp}°C"
        tempMinData = "${data.tempMin}°C"
        tempMaxData = "${data.tempMax}°C"
        sunriseData = data.sunrise!!.toLong()
        sunsetData = data.sunset!!.toLong()
        pressureData = data.pressure!!.toInt()
        humidityData = data.humidity!!
        windSpeedData = data.windSpeed!!

        implementView()

        sr_container.isRefreshing = false
    }

    companion object {
        private const val appId = "48d12086987d15ea4ec7dc69b987effb"
        private val LOCATION_PERMISSION_REQUEST_CODE = 111
    }
}