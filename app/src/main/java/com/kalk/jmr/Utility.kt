package com.kalk.jmr

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.RecommendationsRepository
import com.kalk.jmr.db.genre.GenreRepository
import com.kalk.jmr.ui.recommendations.Token
import java.util.concurrent.Executors


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

fun getRecommendationsRepository(context: Context): RecommendationsRepository {
       // val db =  AppDatabase.getInstance(context)
    return  RecommendationsRepository.getInstance()
}

fun getPlaylistRepository(context: Context): PlaylistRepository {
    val db = AppDatabase.getInstance(context)
    return PlaylistRepository.getInstance(db.trackDao(), db.playListTracksDao(), db.playlistDao())
}


const val fiftyFiveMinutes = 3_300_000L
fun shouldRequestNewToken(token: Token, currentTime: Long): Boolean {
    when {
        token.token.isEmpty() -> return true
        currentTime.minus(token.timestamp) < fiftyFiveMinutes -> return false
        else -> return true
    }
}