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
import com.example.dicodingevent.databinding.FragmentHomeBinding
import de.hdodenhof.circleimageview.CircleImageView
import com.example.dicodingevent.adapter.ListEventFinishedAdapter
import com.example.dicodingevent.adapter.ListEventOngoingAdapterHorizontal
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.ui.viewmodel.HomeViewModel
import com.example.dicodingevent.ui.viewmodel.NetworkStatusViewModel

class HomeFragment : Fragment(), ListEventFinishedAdapter.OnFinishedItemClickListener, ListEventOngoingAdapterHorizontal.OnOngoingItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var ongoingEventAdapter: ListEventOngoingAdapterHorizontal
    private lateinit var finishedEventAdapter: ListEventFinishedAdapter

    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    private lateinit var imageProfile: CircleImageView
    private var imageProfileUrl = "https://w.wallhaven.cc/full/zy/wallhaven-zy759g.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observing network connection status
        networkStatusViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            Log.d("NetworkStatus", "Is Connected: $isConnected")
            if (!isConnected) {
                // Navigate to NetworkStatusFragment if not connected
                val actionNetworkStatus = HomeFragmentDirections.actionNavigationHomeToNetworkStatusFragment()
                findNavController().navigate(actionNetworkStatus)
            }

            if (isConnected) {
                // Refresh data if connected to the internet
                homeViewModel.refreshData()
            }
        }

        // Initialize adapters for ongoing and finished events
        ongoingEventAdapter = ListEventOngoingAdapterHorizontal(this)
        finishedEventAdapter = ListEventFinishedAdapter(this)

        // Set up RecyclerView for ongoing events with horizontal layout
        binding.rvHorizontalHome.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = ongoingEventAdapter
        }

        // Set up RecyclerView for finished events with vertical layout
        binding.rvVerticalHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedEventAdapter
        }

        // Observe list of ongoing events and submit it to the adapter
        homeViewModel.listOngoingEvents.observe(viewLifecycleOwner) { events ->
            ongoingEventAdapter.submitList(events)
        }

        // Observe list of finished events and submit it to the adapter
        homeViewModel.listFinishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        // Observe loading state to show or hide progress bar
        homeViewModel.isLoadingOngoing.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors and handle as necessary
        homeViewModel.isError.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Display error message or take necessary action
                // binding.errorTextView.text = it
            }
        }

        // SwipeRefresh layout to refresh data on swipe down
        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }

        // Load and set profile image using Glide
        imageProfile = binding.ivProfile
        Glide.with(this)
            .load(imageProfileUrl)
            .into(imageProfile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Navigate to DetailFinishedEventFragment when a finished event item is clicked
    override fun onFinishedItemClickListener(event: ListEventsItem) {
        val actionFinishedDetail = HomeFragmentDirections.actionNavigationHomeToDetailFinishedEventFragment()
        findNavController().navigate(actionFinishedDetail)
    }

    // Navigate to DetailOngoingEventFragment when an ongoing event item is clicked
    override fun onOngoingItemClickListener(event: ListEventsItem) {
        val actionOngoing = HomeFragmentDirections.actionNavigationHomeToDetailOngoingEventFragment()
        findNavController().navigate(actionOngoing)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
