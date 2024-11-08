package com.example.dicodingevent.data.retrofit

import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvent(
        @Query("active") active: Int? = null,
        @Query("q") query: String? = null
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") id: Int
    ): Call<EventResponse>
}
