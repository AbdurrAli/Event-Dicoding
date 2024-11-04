package com.example.dicoding.data.retrofit

import com.example.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvent(
        @Query("active") active: Int = 1,
        @Query("q") query: String? = null,

        ): Call<EventResponse>
}