package com.example.dicodingevent.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingevent.adapter.ListEventFinishedAdapter
import com.example.dicodingevent.adapter.ListEventOngoingAdapterVertical
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentSearchBinding
import com.example.dicodingevent.ui.viewmodel.NetworkStatusViewModel
import com.example.dicodingevent.ui.viewmodel.SearchViewModel

class SearchFragment : Fragment(), ListEventFinishedAdapter.OnFinishedItemClickListener,
    ListEventOngoingAdapterVertical.OnOngoingItemClickListener {

    // Nullable binding to prevent memory leaks
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // ViewModel for managing search functionality
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var finishedEventAdapter: ListEventFinishedAdapter

    // ViewModel for monitoring network status
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe network connectivity and navigate to NetworkStatusFragment if offline
        networkStatusViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                val actionNetworkStatus =
                    SearchFragmentDirections.actionNavigationSearchToNetworkStatusFragment()
                findNavController().navigate(actionNetworkStatus)
            }
        }

        // Initialize the adapter for finished events
        finishedEventAdapter = ListEventFinishedAdapter(this)

        // Set up RecyclerView for displaying all events in vertical layout
        binding.rvAllEvent.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = finishedEventAdapter
        }

        // Set up search functionality for filtering events
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Trigger search in ViewModel with new text input
                searchViewModel.searchEvents(newText ?: "")
                return true
            }
        })

        // Observe filtered events and update the adapter list when data changes
        searchViewModel.filteredEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        // Observe all events data and display it in the adapter
        searchViewModel.listAllEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        // Show or hide progress bar based on loading state
        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors and handle them if they occur
        searchViewModel.isError.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Display error message or handle the error as needed
                // binding.errorTextView.text = it
            }
        }

        // Set up swipe-to-refresh functionality to reload data
        binding.swipeRefresh.setOnRefreshListener {
            searchViewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding reference to prevent memory leaks
        _binding = null
    }

    // Handle click event for finished events and navigate to the details page
    override fun onFinishedItemClickListener(event: ListEventsItem) {
        val actionFinished =
            SearchFragmentDirections.actionNavigationSearchToDetailFinishedEventFragment(event.id ?: 0)
        findNavController().navigate(actionFinished)
    }

    // Handle click event for ongoing events and navigate to the details page
    override fun onOngoingItemClickListener(event: ListEventsItem) {
        val actionOngoing =
            SearchFragmentDirections.actionNavigationSearchToDetailOngoingEventFragment(event.id ?: 0)
        findNavController().navigate(actionOngoing)
    }
}
