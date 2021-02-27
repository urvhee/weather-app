package com.zukhruf.weatherapp

import android.content.Context
import com.zukhruf.weatherapp.database.AppDatabase
import com.zukhruf.weatherapp.database.WeatherEntity
import com.zukhruf.weatherapp.network.ConfigRetrofit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainPresenter( private var modelView: MainView, private var context: Context) {

    fun getWeather(lat: String, lon: String, appId: String) {
        modelView.onShowLoading()
        val compositeDisposable = CompositeDisposable()
        val disposable = ConfigRetrofit.retrofit.getWeather(lat, lon, appId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ r ->
                modelView.onSuccess(r)
                modelView.onHideLoading()
            },{ e ->
                modelView.onError(e.localizedMessage)
                modelView.onHideLoading()
            }, {})
        compositeDisposable.add(disposable)
    }

    fun saveCache(model: WeatherEntity) {
        AppDatabase.getInstance(context)?.weatherDao()?.insert(model)
    }

    fun getById(id: Int) {
        val model = AppDatabase.getInstance(context)?.weatherDao()?.getById(id)
        modelView.onSuccessCache(model!!)
    }

    fun updateCache(model: WeatherEntity) {
        AppDatabase.getInstance(context)?.weatherDao()?.update(model.id!!, model.lat!!, model.lon!!, model.weatherMain!!, model.weatherDesc!!,
            model.weatherIcon!!, model.base!!, model.temp!!, model.feelsLike!!, model.tempMin!!,
            model.tempMax!!, model.pressure!!, model.humidity!!, model.visibility!!, model.windSpeed!!,
            model.windDeg!!, model.cloudsAll!!, model.dt!!, model.sysType!!, model.sysId!!, model.sysCountry!!,
            model.sunrise!!, model.sunset!!, model.timezone!!, model.name!!, model.cod!!)
    }

    fun delete(model: WeatherEntity) {
        AppDatabase.getInstance(context)?.weatherDao()?.delete(model.id!!)
    }

    fun getLast() {
        val model = AppDatabase.getInstance(context)?.weatherDao()?.getLast()
        modelView.onSuccessCache(model!!)
    }
}