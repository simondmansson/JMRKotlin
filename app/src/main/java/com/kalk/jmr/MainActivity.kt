package com.kalk.jmr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kalk.jmr.enums.ActivityBroadcast


class MainActivity : AppCompatActivity(), PlayCommands {
    private val CLIENT_ID = "b846f536be0747dbb3b7a8a10946c4be"
    private val REDIRECT_URI = "JMR://spotify/callback"


    private lateinit var navController: NavController
    private lateinit var mSpotifyAppRemote: SpotifyAppRemote
    private lateinit var connectionParams: ConnectionParams
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var mGPS: GPS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup UI
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar_main)

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navController)
        setupWithNavController(bottom_nav, navController)

        mActivityRecognitionClient = ActivityRecognition.getClient(this)
        mActivityRecognitionClient.requestActivityUpdates(30*1000, getActivityDetectionPendingIntent())
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
                    connected()
                }

                override fun onFailure (throwable: Throwable) {
                    Log.e(TAG, throwable.message, throwable)
                }
            }

            SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, connectionListener)

            if(!hasPermissions(applicationContext, GPS_PERMISSIONS)) {
                Log.e(TAG, "no permission");
                ActivityCompat.requestPermissions(this, GPS_PERMISSIONS,
                        1337);
            } else Log.d(TAG, "Permissions previously granted");

        }

        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action.equals(ActivityBroadcast.DETECTED_ACTIVITY_BROADCAST.name)) {
                    val confidence = intent.getIntExtra(ActivityBroadcast.ACTIVITY_CONFIDENCE.name, 0);
                    val type = intent.getStringExtra(ActivityBroadcast.DETECTED_ACTIVITY.name)
                    Log.i(TAG, "received" + type)
                    toast("detected action $type with confidence $confidence")
                }
            }
        };
        LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(mBroadcastReceiver, IntentFilter(ActivityBroadcast.DETECTED_ACTIVITY_BROADCAST.name));

           //TODO if not spotify is installed
          //  navController.navigate(R.id.error_fragment)
    }

    override fun onStop() {
        super.onStop()
        when {
            mSpotifyAppRemote?.isConnected -> SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote)
        }
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

    private fun connected() {
        Log.i(TAG, "Connected")
    }

    override fun play(uris:List<String>) {
        mSpotifyAppRemote?.playerApi.play("spotify:track:${uris[0]}")
        for (song in 1 until uris.size) {
            mSpotifyAppRemote?.playerApi.queue("spotify:track:${uris[song]}")
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
                mGPS = GPS(this)
                toast(mGPS.CurrentLocation())
                return
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
