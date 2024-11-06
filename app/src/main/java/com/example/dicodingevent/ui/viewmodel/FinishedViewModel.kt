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

class FinishedViewModel : ViewModel() {

    // LiveData to hold list of finished events
    private val _listFinishedEvents = MutableLiveData<List<ListEventsItem>>()
    val listFinishedEvents: LiveData<List<ListEventsItem>> = _listFinishedEvents

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error message
    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    // Initialize and load finished events
    init {
        getListFinishedEvents()
    }

    // Refresh data by calling the function to fetch finished events again
    fun refreshData() {
        getListFinishedEvents()
    }

    // Fetch finished events from the API
    private fun getListFinishedEvents() {
        _isLoading.value = true
        _isError.value = null // Reset error every time a new request starts

        // Make API call with 'active = 0' to fetch finished events
        val client = ApiConfig.getApiService().getEvent(active = 0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        if (it.isNotEmpty()) {
                            _listFinishedEvents.value = it.filterNotNull() // Update LiveData with the finished events
                        } else {
                            _isError.value = "No finished events available" // Show a message if no events are available
                        }
                    }
                } else {
                    _isError.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message // Update LiveData with error message on failure
            }
        })
    }
}
