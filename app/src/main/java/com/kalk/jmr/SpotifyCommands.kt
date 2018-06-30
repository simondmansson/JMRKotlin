package com.kalk.jmr

interface SpotifyCommands {
    fun play(uris:List<String>)
    fun requestAuthToken()
}