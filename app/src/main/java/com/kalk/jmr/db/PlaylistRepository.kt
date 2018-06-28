package com.kalk.jmr.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.kalk.jmr.buildJMRWebService
import com.kalk.jmr.db.playlist.HistoryPlaylist
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.playlist.PlaylistDao
import com.kalk.jmr.db.playlistTracks.PlaylistTrack
import com.kalk.jmr.db.playlistTracks.PlaylistTracksDao
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
import com.kalk.jmr.ioThread
import com.kalk.jmr.webService.GenreMessage
import com.kalk.jmr.webService.SpotifyTrack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistRepository private constructor(val trackDao: TrackDao, val playlistTracksDao: PlaylistTracksDao, val playlistDao: PlaylistDao) {

    private var storedPlaylists = playlistDao.selectAll()
    private var storedTracks = trackDao.allTracks()
    private var storedPlaylistTracks = playlistTracksDao.selectAll()
    private val service = buildJMRWebService()
    private val tracksToQueue: MutableLiveData<List<Track>> = MutableLiveData()
    val TracksToPlay: LiveData<List<Track>> = Transformations.map(tracksToQueue, { it })

    private val playlists: LiveData<List<HistoryPlaylist>> = Transformations.map(storedPlaylists, {
        it.map { playlist ->
            val songsUris = storedPlaylistTracks.value?.filter { playlist.id == it.playlist }
            val songs: MutableList<Track> = mutableListOf()
            songsUris?.forEach {plTrack ->
                val track = storedTracks.value?.find { it.uri == plTrack.track }
                if (track != null)
                    songs.add(track)
            }
            HistoryPlaylist(playlist.id ,playlist.title, songs)
        }
    })

    fun getPlaylists(): LiveData<List<HistoryPlaylist>> {
        return playlists
    }

    fun storePlaylist(playlist: Playlist, tracks:List<Track>) {
        ioThread {
            playlistDao.addPlaylist(playlist)
            tracks.forEach {
                trackDao.addTrack(it)
                playlistTracksDao.addPlaylistTrack(PlaylistTrack(playlist.id, it.uri))
            }
        }
    }

    fun removePlaylist(id:String){
        ioThread {
            playlistDao.removePlaylist(id)
        }
    }


    fun requestTracks(token:String, genre: String)  {
        val message = GenreMessage(token, genre.decapitalize())
        Log.i(TAG, message.toString())
        ioThread {
            val callback = object : Callback<List<Track>> {
                override fun onResponse(call: Call<List<Track>>?, response: Response<List<Track>>?) {
                    Log.i(TAG, response?.toString())
                    Log.i(TAG, response?.raw()?.headers().toString())
                    Log.i(TAG, "body: ${response?.body()}")
                    Log.i(TAG, call?.isExecuted.toString())
                    Log.i(TAG, call?.request().toString())
                    Log.i(TAG, call?.request()?.body().toString())

                    if (response!!.isSuccessful) {
                        val songs = response.body()
                        tracksToQueue.postValue(songs!!)
                    }
                }

                override fun onFailure(call: Call<List<Track>>?, t: Throwable?) {
                    Log.e(TAG, "Could not retrieve tracks")
                }
            }
            val req = service.postRecommendationGenre(message).enqueue(callback)
        }

    }

    companion object {
        private var sInstance: PlaylistRepository? = null
        fun getInstance(trackDao: TrackDao, playlistTracksDao: PlaylistTracksDao, playlistDao: PlaylistDao) = sInstance ?:
        synchronized(this) {
            sInstance ?: buildRepository(trackDao, playlistTracksDao, playlistDao).also { sInstance = it }
        }

        private fun buildRepository(trackDao: TrackDao, playlistTracksDao: PlaylistTracksDao, playlistDao: PlaylistDao): PlaylistRepository {
            return PlaylistRepository(trackDao, playlistTracksDao, playlistDao)
        }

        val TAG = PlaylistRepository::class.java.simpleName
    }
}