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

class HomeViewModel : ViewModel() {

    // LiveData to hold ongoing and finished events
    private val _listOngoingEvents = MutableLiveData<List<ListEventsItem>>()
    val listOngoingEvents: LiveData<List<ListEventsItem>> = _listOngoingEvents

    private val _listFinishedEvents = MutableLiveData<List<ListEventsItem>>()
    val listFinishedEvents: LiveData<List<ListEventsItem>> = _listFinishedEvents

    // LiveData for loading states for both event types
    private val _isLoadingOngoing = MutableLiveData<Boolean>()
    val isLoadingOngoing: LiveData<Boolean> = _isLoadingOngoing

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    // LiveData for error messages
    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    init {
        getListOngoingEvents()
        getListFinishedEvents()
    }

    // Refresh both ongoing and finished events
    fun refreshData() {
        getListOngoingEvents()
        getListFinishedEvents()
    }

    // Fetch ongoing events
    private fun getListOngoingEvents() {
        _isLoadingOngoing.value = true
        _isError.value = null // Reset error every time a new request starts

        val client = ApiConfig.getApiService().getEvent(active = 1) // 1 for ongoing events
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingOngoing.value = false
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        _listOngoingEvents.value = it.filterNotNull() // Update LiveData with ongoing events
                    }
                } else {
                    _isError.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingOngoing.value = false
                _isError.value = t.message // Update LiveData with the error message on failure
            }
        })
    }

    // Fetch finished events
    private fun getListFinishedEvents() {
        _isLoadingFinished.value = true
        _isError.value = null // Reset error every time a new request starts

        val client = ApiConfig.getApiService().getEvent(active = 0) // 0 for finished events
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoadingFinished.value = false
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        _listFinishedEvents.value = it.filterNotNull() // Update LiveData with finished events
                    }
                } else {
                    _isError.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoadingFinished.value = false
                _isError.value = t.message // Update LiveData with the error message on failure
            }
        })
    }
}
