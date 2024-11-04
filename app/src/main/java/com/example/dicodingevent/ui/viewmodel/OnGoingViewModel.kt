package com.example.dicodingevent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicoding.data.retrofit.ApiConfig
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

    private fun getListOngoingEvents() {
        _isLoading.value = true
        _isError.value = null // Reset error setiap kali memulai permintaan baru

        val client = ApiConfig.getApiService().getEvent(active = 1) // 1 for ongoing events
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.listEvents?.let {
                        _listOngoingEvents.value = it.filterNotNull() // Update LiveData with ongoing events
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
}
