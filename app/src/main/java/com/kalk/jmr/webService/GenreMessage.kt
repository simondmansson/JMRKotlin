package com.kalk.jmr.webService

import com.google.gson.GsonBuilder



data class GenreMessage(val token:String, val genre:String) {

    override fun toString(): String {
        return GsonBuilder().create().toJson(this, GenreMessage::class.java)
   }
}