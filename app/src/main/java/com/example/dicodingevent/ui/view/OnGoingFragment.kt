package com.example.dicodingevent.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import com.example.dicodingevent.adapter.ListEventOngoingAdapterVertical
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentOnGoingBinding
import com.example.dicodingevent.ui.viewmodel.NetworkStatusViewModel
import com.example.dicodingevent.ui.viewmodel.OnGoingViewModel

class OnGoingFragment : Fragment(), ListEventOngoingAdapterVertical.OnOngoingItemClickListener {

    // Using nullable binding to prevent memory leaks
    private var _binding: FragmentOnGoingBinding? = null
    private val binding get() = _binding!!

    // ViewModel for ongoing events
    private val ongoingViewModel: OnGoingViewModel by viewModels()
    private lateinit var ongoingEventAdapter: ListEventOngoingAdapterVertical

    // ViewModel for network status
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    // Profile image setup
    private lateinit var imageProfile: CircleImageView
    private var imageProfileUrl = "https://w.wallhaven.cc/full/zy/wallhaven-zy759g.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initializing view binding
        _binding = FragmentOnGoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe network connectivity status
        networkStatusViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            Log.d("NetworkStatus", "Is Connected: $isConnected")
            if (!isConnected) {
                // Navigate to NetworkStatusFragment if there is no connection
                val actionNetworkStatus = OnGoingFragmentDirections.actionNavigationUpcomingToNetworkStatusFragment()
                findNavController().navigate(actionNetworkStatus)
            }
        }

        // Initialize adapter for ongoing events
        ongoingEventAdapter = ListEventOngoingAdapterVertical(this)

        // Set up RecyclerView for ongoing events (vertical layout)
        binding.rvVerticalHome.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ongoingEventAdapter
        }

        // Observe ongoing events data and update adapter
        ongoingViewModel.listOngoingEvents.observe(viewLifecycleOwner) { events ->
            ongoingEventAdapter.submitList(events)
        }

        // Observe loading state and show/hide progress bar
        ongoingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors and handle them (currently commented out)
        ongoingViewModel.isError.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Display error message or handle the error as needed
                // binding.errorTextView.text = it
            }
        }

        // Load profile image using Glide library
        imageProfile = binding.ivProfile
        Glide.with(this)
            .load(imageProfileUrl)
            .into(imageProfile)

        // Set up swipe-to-refresh action to reload data
        binding.swipeRefresh.setOnRefreshListener {
            ongoingViewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding reference to avoid memory leaks
        _binding = null
    }

    // Handle item click for ongoing event and navigate to event details
    override fun onOngoingItemClickListener(event: ListEventsItem) {
        val actionOngoing = OnGoingFragmentDirections.actionNavigationUpcomingToDetailOngoingEventFragment()
        findNavController().navigate(actionOngoing)
    }
}
