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
import com.google.gson.JsonIOException
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.location.Coordinates
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.enums.ActivityBroadcast
import com.kalk.jmr.enums.ValidActivity
import com.kalk.jmr.enums.validAcitvityBuilder
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
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.doAsync
import java.io.IOException
import java.lang.RuntimeException
import java.util.*


@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), SpotifyCommands {

    private lateinit var navController: NavController
    private lateinit var mSpotifyAppRemote: SpotifyAppRemote
    private lateinit var connectionParams: ConnectionParams
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var preferences: SharedPreferences
    private lateinit var settings: SettingsViewModel
    private lateinit var recommendationsViewModel: RecommendationsViewModel

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

        recommendationsViewModel.setActivity(validAcitvityBuilder(ValidActivity.STILL.title)) //setActiviy to still on startup

        with(settings) {
            activity.value = preferences.getBoolean(SWITCH_ACTIVITY, true)
            location.value = preferences.getBoolean(SWITCH_LOCATION, true)
            time.value = preferences.getBoolean(SWITCH_TIME, true)
        }

        mActivityRecognitionClient = ActivityRecognition.getClient(this)
        mActivityRecognitionClient.requestActivityUpdates(30 * 1000, getActivityDetectionPendingIntent())
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val storedUserLocation = recommendationsViewModel.currentLocation.value
                ?: UserLocation("", "", Coordinates(0.0, 0.0))

        if (shouldRequestNewLocation(storedUserLocation, preferences.getLong(LOCATION_TIMESTAMP, Long.MAX_VALUE), System.currentTimeMillis()))
            getLatestKnownLocation()
    }

    override fun onStart() {
        super.onStart()

        //Setup Spotify
        if (SpotifyAppRemote.isSpotifyInstalled(applicationContext)) {
            requestAuthToken()

            connectionParams = ConnectionParams.Builder(CLIENT_ID)
                    .showAuthView(true)
                    .setRedirectUri(REDIRECT_URI)
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
                    if(confidence > 75)
                        recommendationsViewModel.setActivity(validAcitvityBuilder(type))
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
            settings.apply {
                putBoolean(SWITCH_ACTIVITY, activity.value ?: true)
                putBoolean(SWITCH_LOCATION, location.value ?: true)
                putBoolean(SWITCH_TIME, time.value ?: true)
            }
            recommendationsViewModel.apply {
                putLong(TOKEN_TIMESTAMP, authToken.value?.timestamp ?: Long.MAX_VALUE)
                putString(TOKEN_STRING, authToken.value?.token)
            }
            apply()
        }
        mActivityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
    }

    override fun onStop() {
        super.onStop()
        try { if (mSpotifyAppRemote.isConnected) SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote) }
        catch (e:RuntimeException) { Log.e(TAG, e.message) }
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
            R.id.aboutFragment -> navController.navigate(R.id.aboutFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun play(uris: List<String>) {
        Log.i(TAG, "Songs to play ${uris.size}")
            mSpotifyAppRemote.apply {
            if (isConnected) {
                val playerState = playerApi.playerState.await()
                if(playerState.isSuccessful) {
                    when {
                        playerState.data.isPaused -> {
                            playerApi.play(uris[0])
                            for (song in 1 until uris.size) {
                                playerApi.queue(uris[song])
                            }
                            longSnackbar(this@MainActivity.view_pager_main,
                                    "Playing track${if(uris.size > 1) "s" else ""}")
                        }
                        else -> {
                            for (song in 0 until uris.size) {
                                playerApi.queue(uris[song])
                            }
                            longSnackbar(this@MainActivity.view_pager_main,
                                    "Queueing track${if(uris.size > 1) "s" else ""}")
                        }
                    }
                }
            }
        }
    }

    override fun requestAuthToken() {
        //If recommendationsViewModel have a token it will be used. Else we get it from preferences or default value
        val token =   if(recommendationsViewModel.authToken.value != null)
                                recommendationsViewModel.authToken.value!!
                            else Token(preferences.getString(TOKEN_STRING, ""), preferences.getLong(TOKEN_TIMESTAMP, Long.MAX_VALUE))


        if(shouldRequestNewToken(token, System.currentTimeMillis())) {
            val builder = AuthenticationRequest.Builder(
                                                    CLIENT_ID,
                                                    AuthenticationResponse.Type.TOKEN,
                                                    REDIRECT_URI)
            builder.setScopes(arrayOf("app-remote-control", "user-read-private", "streaming"))
            val request = builder.build()
            AuthenticationClient.openLoginActivity(this, 1442, request)
        } else  {
            recommendationsViewModel.authToken.value = token
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 1442) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    Log.i(TAG, response.accessToken)
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
                getLatestKnownLocation()
                return
            }
        }
    }

    //thanks google
    private fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityRecognitionService::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getLatestKnownLocation() {
        if (!hasPermissions(this, GPS_PERMISSIONS)) {
            Log.e(TAG, "no permission")
            ActivityCompat.requestPermissions(this, GPS_PERMISSIONS, 1337)
        } else {
            Log.d(TAG, "Permissions previously granted")
            mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
                doAsync {
                    val db = AppDatabase.getInstance(applicationContext)
                    val fromLong = it.longitude - 0.0001
                    val toLong = it.longitude + 0.0001
                    val fromLat = it.latitude - 0.0001
                    val toLat = it.latitude + 0.0001
                    val previousLocationInRange: UserLocation? = db.locationDao().inRangeOfCoordinates(fromLong,fromLat, toLong, toLat)
                    if (previousLocationInRange != null) {
                        recommendationsViewModel.setLocation(previousLocationInRange)
                    } else {

                        try {
                            val service = buildGoogleApiService()
                            val res = service.addressFromCoordinates("${it.latitude}, ${it.longitude}", "AIzaSyAjijhgnmvidgyjVXYNumXu4CH8_fPHzcc").execute()

                            if(res.isSuccessful) {
                                Log.i(TAG, "${res.body()}")
                                val locs = res.body()!!
                                Log.i(TAG, locs.results[1].formattedAddress)
                                val symbolic = locs.results[1].addressComponent[0].short_name
                                val userLoc = UserLocation(UUID.randomUUID().toString(), symbolic, Coordinates(it.longitude, it.latitude))
                                db.locationDao().addLocation(userLoc)
                                recommendationsViewModel.setLocation(userLoc)
                            } else {
                                Log.e(TAG, "Error retrieving location data: ${res.code()} ${res.errorBody()}")
                            }
                        } catch (e: IOException) {
                            Log.e(TAG, e.message)
                        }  catch (e: JsonIOException) {
                            Log.e(TAG, e.message)
                        } catch (e: Exception) {
                            Log.e(TAG, e.message)
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
