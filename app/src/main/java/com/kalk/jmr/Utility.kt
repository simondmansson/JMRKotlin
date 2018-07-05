package com.kalk.jmr

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.genre.GenreRepository
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.ui.recommendations.Token
import com.kalk.jmr.webService.GoogleLocationService
import com.kalk.jmr.webService.JMRWebService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val GPS_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

fun hasPermissions(context: Context?, allPermissionNeeded: Array<String>): Boolean {
    for (permission in allPermissionNeeded)
        if (ActivityCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED)
            return false

    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null)
        for (permission in allPermissionNeeded)
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false
    return true
}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun getGenreRepository(context: Context): GenreRepository {
    return  GenreRepository.getInstance(AppDatabase.getInstance(context).genreDao())
}

fun getPlaylistRepository(context: Context): PlaylistRepository {
    val db = AppDatabase.getInstance(context)
    return PlaylistRepository.getInstance(db.trackDao(), db.playListTracksDao(), db.playlistDao())
}


const val fiftyFiveMinutes = 3_300_000L
fun shouldRequestNewToken(token: Token, currentTime: Long): Boolean {
    when {
        token.token == "" -> return true
        currentTime.minus(token.timestamp) < fiftyFiveMinutes -> return false
        else -> return true
    }
}

const val fifteenMinutes = 900_000L

fun shouldRequestNewLocation(location: UserLocation, savedTime:Long, currentTime: Long): Boolean {
    when {
        location.id.isEmpty() -> return true
        currentTime.minus(savedTime) < fifteenMinutes -> return false
        else -> return true
    }
}

fun  buildJMRWebService(baseUrl:String = "https://jmr-backend.herokuapp.com" ): JMRWebService {
    val okHttpClient = OkHttpClient.Builder()
    okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
    okHttpClient.readTimeout(60, TimeUnit.SECONDS)
    okHttpClient.writeTimeout(60, TimeUnit.SECONDS)
    okHttpClient.retryOnConnectionFailure(true)

    val retrofit =  Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

    return retrofit.create(JMRWebService::class.java)
}

fun  buildGoogleApiService(baseUrl:String = "https://maps.googleapis.com" ): GoogleLocationService {
    val okHttpClient = OkHttpClient.Builder()
    okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
    okHttpClient.readTimeout(60, TimeUnit.SECONDS)
    okHttpClient.writeTimeout(60, TimeUnit.SECONDS)
    okHttpClient.retryOnConnectionFailure(true)

    val retrofit =  Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

    return retrofit.create(GoogleLocationService::class.java)
}

