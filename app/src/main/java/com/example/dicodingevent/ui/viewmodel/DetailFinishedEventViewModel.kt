package com.example.dicodingevent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFinishedEventViewModel : ViewModel() {

    // LiveData for loading status
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    // LiveData for selected event details
    private val _selectedEvent = MutableLiveData<ListEventsItem?>()
    val selectedEvent: LiveData<ListEventsItem?> = _selectedEvent

    // LiveData for list of events
    private val _eventList = MutableLiveData<List<ListEventsItem?>?>()
    val eventList: LiveData<List<ListEventsItem?>?> = _eventList

    // LiveData for remaining quota of selected event
    private val _remainingQuota = MutableLiveData<Int>()
    val remainingQuota: LiveData<Int> = _remainingQuota

    // Function to get details of a selected event by ID
    fun getEventDetail(id: Int) {
        _isLoading.value = true
        _isError.value = null

        // Panggil API untuk mengambil detail acara
        ApiConfig.getApiService().getEventDetail(id).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    eventResponse?.let {
                        // Mengambil event tunggal dari response
                        _selectedEvent.value = it.event
                        calculateRemainingQuota() // Recalculate remaining quota when new event detail is fetched
                    }
                } else {
                    _isError.value = "Failed to retrieve event details"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message
            }
        })
    }

    // Function to fetch list of events
    fun fetchEvents() {
        _isLoading.value = true
        _isError.value = null

        // Panggil API untuk mengambil daftar acara
        ApiConfig.getApiService().getEvent().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    eventResponse?.let {
                        // Menyimpan daftar acara dari response
                        _eventList.value = it.listEvents
                    }
                } else {
                    _isError.value = "Failed to retrieve events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message
            }
        })
    }

    // Function to calculate remaining quota for the selected event
    private fun calculateRemainingQuota() {
        val selectedEvent = _selectedEvent.value
        selectedEvent?.let {
            val quota = it.quota ?: 0
            val registrants = it.registrants ?: 0
            _remainingQuota.value = quota - registrants // Calculate remaining quota
        }
    }
}

