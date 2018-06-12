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
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), PlayCommands {
    private val CLIENT_ID = "b846f536be0747dbb3b7a8a10946c4be"
    private val REDIRECT_URI = "JMR://spotify/callback"
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var navController: NavController
    private lateinit var mSpotifyAppRemote: SpotifyAppRemote
    private lateinit var connectionParams: ConnectionParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup UI
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar_main)

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(this, navController)
        setupWithNavController(bottom_nav, navController)
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

        }
           //TODO if not spotify is installed
          //  navController.navigate(R.id.error_fragment)
    }

    override fun onStop() {
        super.onStop()
        when {
            mSpotifyAppRemote?.isConnected -> SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote)
        }
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
}
