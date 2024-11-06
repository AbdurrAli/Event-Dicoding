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

class OnGoingViewModel : ViewModel() {

    private val _listOngoingEvents = MutableLiveData<List<ListEventsItem>>()
    val listOngoingEvents: LiveData<List<ListEventsItem>> = _listOngoingEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    init {
        getListOngoingEvents()
    }

    fun refreshData() {
        getListOngoingEvents()
    }

    private fun getListOngoingEvents() {
        _isLoading.value = true // This is valid as it is a Boolean (non-nullable)
        _isError.value = null // This is fine as _isError is of type String?

        val client = ApiConfig.getApiService().getEvent(active = 1) // 1 for ongoing events
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false // This is valid as it is a Boolean (non-nullable)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents?.filterNotNull() ?: emptyList()

                    if (events.isEmpty()) {
                        _isError.value = "No ongoing events available"
                    } else {
                        _listOngoingEvents.value = events // Update LiveData with ongoing events
                    }
                } else {
                    _isError.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false // This is valid as it is a Boolean (non-nullable)
                // Check the type of error to provide more specific messages
                _isError.value = when (t) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Request timeout. Please try again."
                    else -> t.message ?: "Unknown error occurred"
                }
            }
        })
    }
}
