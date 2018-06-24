package com.kalk.jmr

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.location.Coordinates
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.enums.ActivityBroadcast
import com.kalk.jmr.ui.genres.GenresViewModel
import com.kalk.jmr.ui.genres.GenresViewModelFactory
import com.kalk.jmr.ui.recommendations.RecommendationsViewModel
import com.kalk.jmr.ui.recommendations.RecommendationsViewModelFactory
import com.kalk.jmr.ui.recommendations.Token
import com.kalk.jmr.ui.settings.SettingsViewModel
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast
import java.util.*


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), PlayCommands {

    private lateinit var navController: NavController
    private lateinit var mSpotifyAppRemote: SpotifyAppRemote
    private lateinit var connectionParams: ConnectionParams
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var preferences: SharedPreferences
    private lateinit var settings: SettingsViewModel
    private lateinit var recommendationsViewModel: RecommendationsViewModel
    private lateinit var genreViewModel: GenresViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup UI
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar_main)
        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        bottom_nav.setupWithNavController(navController)

        preferences = getSharedPreferences("com.kalk.jmr.sharedPreferences", Context.MODE_PRIVATE)
        settings = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        recommendationsViewModel = ViewModelProviders.of(this,
                RecommendationsViewModelFactory(
                        getPlaylistRepository(applicationContext)))
                .get(RecommendationsViewModel::class.java)

        genreViewModel = ViewModelProviders.of(this,
                GenresViewModelFactory(
                        getGenreRepository(applicationContext)))
                .get(GenresViewModel::class.java)

        genreViewModel.chosenGenre.value = preferences.getInt(CHOSEN_GENRE, 0)

        genreViewModel.genreText.observe(this, android.arch.lifecycle.Observer {
            toast(it ?: "null")
        })

        with(settings) {
            activity.value = preferences.getBoolean(SWITCH_ACTIVITY, true)
            location.value = preferences.getBoolean(SWITCH_LOCATION, true)
            time.value = preferences.getBoolean(SWITCH_TIME, true)
        }

        mActivityRecognitionClient = ActivityRecognition.getClient(this)
        if (settings.activity.value!!)
            mActivityRecognitionClient.requestActivityUpdates(30 * 1000, getActivityDetectionPendingIntent())

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val storedUserLocation = recommendationsViewModel.currentLocation.value
                ?: UserLocation(preferences.getString(LOCATION_ID, ""),
                        Coordinates(preferences.getLong(LOCATION_LONGITUDE, 0).toDouble(), preferences.getLong(LOCATION_LATITUDE, 0).toDouble())
                )

        if (shouldRequestNewLocation(storedUserLocation, preferences.getLong(LOCATION_TIMESTAMP, Long.MAX_VALUE), System.currentTimeMillis()))
            getLatestKnownLocation()
    }

    override fun onStart() {
        super.onStart()

        //Setup Spotify
        if (SpotifyAppRemote.isSpotifyInstalled(applicationContext)) {

            //If recommendationsViewModel have a token it will be used. Else we get it from preferences or default value
            val token = recommendationsViewModel.authToken.value ?: Token(
                    preferences.getString(TOKEN_STRING, ""),
                    preferences.getLong(TOKEN_TIMESTAMP, Long.MAX_VALUE)
            )

            if (shouldRequestNewToken(token, System.currentTimeMillis())) {
                val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                builder.setScopes(arrayOf("app-remote-control", "user-read-recently-played", "user-read-private", "streaming"))
                val request = builder.build()
                AuthenticationClient.openLoginActivity(this, 1442, request)
            }

            connectionParams = ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build()

            val connectionListener = object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e(TAG, throwable.message, throwable)
                }
            }

            SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, connectionListener)
        }
        //TODO if not spotify is installed
        //  navController.navigate(R.id.error_fragment)

        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == ActivityBroadcast.DETECTED_ACTIVITY_BROADCAST.name) {
                    val confidence = intent.getIntExtra(ActivityBroadcast.ACTIVITY_CONFIDENCE.name, 0)
                    val type = intent.getStringExtra(ActivityBroadcast.DETECTED_ACTIVITY.name)
                    Log.i(TAG, "received $type with confidence $confidence")
                    recommendationsViewModel.setActivity(type)
                }
            }
        }
        LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(mBroadcastReceiver, IntentFilter(ActivityBroadcast.DETECTED_ACTIVITY_BROADCAST.name))
    }

    override fun onResume() {
        super.onResume()
        mActivityRecognitionClient.requestActivityUpdates(30 * 1000, getActivityDetectionPendingIntent())
    }

    override fun onPause() {
        super.onPause()
        with(preferences.edit()) {
            putBoolean(SWITCH_ACTIVITY, settings.activity.value ?: true)
            putBoolean(SWITCH_LOCATION, settings.location.value ?: true)
            putBoolean(SWITCH_TIME, settings.time.value ?: true)
            putInt(CHOSEN_GENRE, genreViewModel.chosenGenre.value ?: 0)
            putLong(TOKEN_TIMESTAMP, recommendationsViewModel.authToken.value?.timestamp
                    ?: Long.MAX_VALUE)
            putString(TOKEN_STRING, recommendationsViewModel.authToken.value?.token)
            apply()
        }
        mActivityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
    }

    override fun onStop() {
        super.onStop()
        if (mSpotifyAppRemote.isConnected) {
            SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote)
        }
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mBroadcastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settingsFragment -> navController.navigate(R.id.settingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun play(uris: List<String>) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 1442) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d(TAG, response.accessToken)
                    toast(response.accessToken)
                    recommendationsViewModel.authToken.value = Token(response.accessToken, System.currentTimeMillis())
                }
                AuthenticationResponse.Type.ERROR -> Log.e(TAG, response.error)
                else -> Log.e(TAG, "Im in else, why?")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1337 -> {
                Log.d(TAG, "Permissions Granted")
                return
            }
        }
    }

    fun getLatestKnownLocation() {
        if (!hasPermissions(this, GPS_PERMISSIONS)) {
            Log.e(TAG, "no permission")
            ActivityCompat.requestPermissions(this, GPS_PERMISSIONS,
                    1337)
        } else {
            Log.d(TAG, "Permissions previously granted")
            mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
                toast("long ${it.longitude} and lat ${it.latitude}")
                ioThread {
                    val db = AppDatabase.getInstance(applicationContext)
                    //TODO HELPER FUNCTION FOR RETURNING DIFFS
                    val previousLocationInRange: UserLocation? = db.locationDao().inRangeOfCoordinates(it.longitude, it.latitude, it.longitude, it.latitude)

                    if (previousLocationInRange != null) {
                        recommendationsViewModel.setLocation(previousLocationInRange)
                        with(preferences.edit()) {
                            putString(LOCATION_ID, previousLocationInRange.id)
                            putLong(LOCATION_LONGITUDE, previousLocationInRange.coordinates.longitude.toLong())
                            putLong(LOCATION_LATITUDE, previousLocationInRange.coordinates.latitude.toLong())
                            putLong(LOCATION_TIMESTAMP, System.currentTimeMillis())
                            apply()
                        }

                    } else {
                        val userLoc = UserLocation(UUID.randomUUID().toString(), Coordinates(it.longitude, it.latitude))
                        recommendationsViewModel.setLocation(userLoc)
                        db.locationDao().addLocation(userLoc)
                        with(preferences.edit()) {
                            putString(LOCATION_ID, userLoc.id)
                            putLong(LOCATION_LONGITUDE, userLoc.coordinates.longitude.toLong())
                            putLong(LOCATION_LATITUDE, userLoc.coordinates.latitude.toLong())
                            putLong(LOCATION_TIMESTAMP, System.currentTimeMillis())
                            apply()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
