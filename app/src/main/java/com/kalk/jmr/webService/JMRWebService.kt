package com.kalk.jmr.webService

import com.kalk.jmr.db.track.Track
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST



interface JMRWebService {

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("/recommendations/genre")
    fun postRecommendationGenre(@Body message: GenreMessage): Call<List<Track>>

    @Headers("Accept: application/json")
    @POST("/recommendations/history")
    fun postRecommendationHistory(@Body message: HistoryMessage): Call<List<Track>>

}