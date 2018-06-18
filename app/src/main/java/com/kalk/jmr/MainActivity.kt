package com.kalk.jmr

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kalk.jmr.enums.ActivityBroadcast
import com.kalk.jmr.ui.recommendations.RecommendationsViewModel
import com.kalk.jmr.ui.settings.SettingsViewModel
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), PlayCommands {
    private val CLIENT_ID = "b846f536be0747dbb3b7a8a10946c4be"
    private val REDIRECT_URI = "JMR://spotify/callback"
    private val SWITCH_ACTIVITY = "SWITCH_ACTIVITY"
    private val SWITCH_LOCATION = "SWITCH_LOCATION"
    private val SWITCH_TIME = "SWITCH_TIME"
    private val CHOSEN_GENRE = "CHOSEN_GENRE"

    private lateinit var navController: NavController
    private lateinit var mSpotifyAppRemote: SpotifyAppRemote
    private lateinit var connectionParams: ConnectionParams
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var preferences: SharedPreferences
    private lateinit var settings: SettingsViewModel
    private lateinit var recommendations: RecommendationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup UI
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar_main)
        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navController)
        setupWithNavController(bottom_nav, navController)

        preferences = getSharedPreferences("com.kalk.jmr.sharedPreferences", Context.MODE_PRIVATE)
        settings = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        with(settings) {
            activity.value = preferences.getBoolean(SWITCH_ACTIVITY, true)
            location.value = preferences.getBoolean(SWITCH_LOCATION, true)
            time.value = preferences.getBoolean(SWITCH_TIME, true)
        }

        mActivityRecognitionClient = ActivityRecognition.getClient(this)
        if (settings.activity.value!!)
            mActivityRecognitionClient.requestActivityUpdates(30*1000, getActivityDetectionPendingIntent())

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStart() {
        super.onStart()

        if(SpotifyAppRemote.isSpotifyInstalled(applicationContext)) {
            //Setup Spotify
            connectionParams = ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build()

            val connectionListener = object: Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                }

                override fun onFailure (throwable: Throwable) {
                    Log.e(TAG, throwable.message, throwable)
                }
            }

            SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, connectionListener)
        }
            //TODO if not spotify is installed
            //  navController.navigate(R.id.error_fragment)

        recommendations = ViewModelProviders.of(this).get(RecommendationsViewModel::class.java)
        recommendations.setGenre(preferences.getString(CHOSEN_GENRE, "None"))

        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == ActivityBroadcast.DETECTED_ACTIVITY_BROADCAST.name) {
                    val confidence = intent.getIntExtra(ActivityBroadcast.ACTIVITY_CONFIDENCE.name, 0)
                    val type = intent.getStringExtra(ActivityBroadcast.DETECTED_ACTIVITY.name)
                    Log.i(TAG, "recived $type with confidence $confidence")
                    recommendations.setActivity(type)
                }
            }
        }
        LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(mBroadcastReceiver, IntentFilter(ActivityBroadcast.DETECTED_ACTIVITY_BROADCAST.name))



        if(!hasPermissions(applicationContext, GPS_PERMISSIONS)) {
            Log.e(TAG, "no permission")
            ActivityCompat.requestPermissions(this, GPS_PERMISSIONS,
                    1337)
        } else {
            Log.d(TAG, "Permissions previously granted")
            if(settings.location.value!!) {
                mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    toast("long ${it.longitude} untz lat ${it.latitude}")
                    recommendations.setLocation("long ${it.longitude} untz lat ${it.latitude}")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(settings.activity.value!!)
            mActivityRecognitionClient.requestActivityUpdates(30*1000, getActivityDetectionPendingIntent())
    }

    override fun onPause() {
        super.onPause()
        with(preferences.edit()) {
            putBoolean(SWITCH_ACTIVITY, settings.activity.value ?: true)
            putBoolean(SWITCH_LOCATION, settings.location.value ?: true)
            putBoolean(SWITCH_TIME, settings.time.value ?: true)
            putString(CHOSEN_GENRE, recommendations.getGenre().value ?: "None")
            apply()
        }

        if(settings.activity.value!!)
            mActivityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
    }

    override fun onStop() {
        super.onStop()
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote)
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mBroadcastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.settingsFragment -> navController.navigate(R.id.settingsFragment)
            R.id.search -> toast("fuck u")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun play(uris:List<String>) {
        mSpotifyAppRemote.playerApi.play("spotify:track:${uris[0]}")
        for (song in 1 until uris.size) {
            mSpotifyAppRemote.playerApi.queue("spotify:track:${uris[song]}")
            Log.e(TAG, uris[song])
        }
    }

    //thanks google
    private fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityRecognitionService::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1337 -> {
                Log.d(TAG, "Permissions Granted")
                return
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
