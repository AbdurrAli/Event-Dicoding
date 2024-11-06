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

class SearchViewModel : ViewModel() {

    private val _listAllEvents = MutableLiveData<List<ListEventsItem>>()
    val listAllEvents: LiveData<List<ListEventsItem>> = _listAllEvents

    private val _filteredEvents = MutableLiveData<List<ListEventsItem>>()
    val filteredEvents: LiveData<List<ListEventsItem>> = _filteredEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    init {
        getListAllEvents()
    }

    fun refreshData() {
        getListAllEvents()
    }

    private fun getListAllEvents() {
        _isLoading.value = true
        _isError.value = null // Reset error each time a new request is started

        val client = ApiConfig.getApiService().getEvent(active = -1) // -1 for all events
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        val nonNullEvents = it.filterNotNull() // Ensure no null elements
                        _listAllEvents.value = nonNullEvents // Update LiveData with all events
                        _filteredEvents.value = nonNullEvents // Initially, show all events in filtered list
                    } ?: run {
                        _listAllEvents.value = emptyList() // Handle null case
                        _filteredEvents.value = emptyList() // Handle null case
                        _isError.value = "No events available"
                    }
                } else {
                    _isError.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message // Update LiveData with the error message on failure
            }
        })
    }

    fun searchEvents(query: String) {
        // Filter based on the event name (ignore case)
        val filteredList = _listAllEvents.value?.filter {
            it.name?.contains(query, ignoreCase = true) == true
        } ?: emptyList()

        _filteredEvents.value = filteredList
    }
}
