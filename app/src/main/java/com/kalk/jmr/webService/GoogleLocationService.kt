package com.kalk.jmr.webService

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GoogleLocationService {

    //https://maps.googleapis.com/maps/api
    @Headers("Accept: application/json")
    @GET("/maps/api/geocode/json")
    fun addressFromCoordinates(@Query("latlng") latlng:String, @Query("key") key:String): Call<GoogleLocationResult>
}