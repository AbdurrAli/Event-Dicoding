package com.example.dicodingevent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.response.ListEventsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailOngoingEventViewModel : ViewModel() {

    // LiveData for loading status
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for list of events
    private val _eventList = MutableLiveData<List<ListEventsItem?>?>()
    val eventList: LiveData<List<ListEventsItem?>?> = _eventList

    // LiveData for error messages
    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    // LiveData for selected event details
    private val _selectedEvent = MutableLiveData<ListEventsItem?>()
    val selectedEvent: LiveData<ListEventsItem?> = _selectedEvent

    // LiveData for remaining quota of selected event
    private val _remainingQuota = MutableLiveData<Int>()
    val remainingQuota: LiveData<Int> = _remainingQuota

    // Function to retrieve list of ongoing events from the API
     fun getEventDetail(id: Int) {
        _isLoading.value = true
        _isError.value = null

        // Make API call with 'active = 1' to get ongoing events
        ApiConfig.getApiService().getEventDetail(id).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    eventResponse?.let {
                        _selectedEvent.value = it.event
                        calculateRemainingQuota() // Recalculate remaining quota when new event detail is fetched
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message
            }
        })
    }

    fun fetchEvents() {
        _isLoading.value = true
        _isError.value = null

        ApiConfig.getApiService().getEvent().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val eventResponse = response.body()
                    eventResponse?.let {
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

    // Function to set a specific event as selected
    fun setSelectedEvent(event: ListEventsItem?) {
        _selectedEvent.value = event
        // Recalculate remaining quota when a new event is selected
        calculateRemainingQuota()
    }
}
