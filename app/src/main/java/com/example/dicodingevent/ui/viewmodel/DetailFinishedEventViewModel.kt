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

class DetailFinishedEventViewModel : ViewModel() {

    // LiveData for the list of finished events
    private val _listFinishedEvents = MutableLiveData<List<ListEventsItem>>()

    // LiveData for loading status
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    // LiveData for selected event details
    private val _selectedEvent = MutableLiveData<ListEventsItem?>()
    val selectedEvent: LiveData<ListEventsItem?> = _selectedEvent

    // LiveData for remaining quota of selected event
    private val _remainingQuota = MutableLiveData<Int>()
    val remainingQuota: LiveData<Int> = _remainingQuota

    // Initialize by loading the list of finished events
    init {
        getListFinishedEvents()
    }

    // Function to refresh data by re-fetching the list of finished events
    fun refreshData() {
        getListFinishedEvents()
    }

    // Function to retrieve list of finished events from the API
    private fun getListFinishedEvents() {
        _isLoading.value = true
        _isError.value = null

        // Make API call with 'active = 0' to get finished events
        val client = ApiConfig.getApiService().getEvent(active = 0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let { events ->
                        _listFinishedEvents.value = events.filterNotNull() // Populate with non-null events

                        // Set the first event as selected if no event is selected yet
                        if (_selectedEvent.value == null) {
                            _selectedEvent.value = _listFinishedEvents.value?.firstOrNull()
                        }

                        calculateRemainingQuota() // Calculate remaining quota for the selected event
                    }
                } else {
                    // Set error message if the response is unsuccessful
                    _isError.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = t.message // Set error message in case of failure
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
