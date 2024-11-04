package com.example.dicodingevent.ui.view

import android.os.Bundle
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

class HomeFragment : Fragment(), ListEventFinishedAdapter.OnFinishedItemClickListener,ListEventOngoingAdapterHorizontal.OnOngoingItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var ongoingEventAdapter: ListEventOngoingAdapterHorizontal
    private lateinit var finishedEventAdapter: ListEventFinishedAdapter

    private lateinit var imageProfile: CircleImageView
    private var imageProfileUrl = "https://w.wallhaven.cc/full/zy/wallhaven-zy759g.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize adapters
        ongoingEventAdapter = ListEventOngoingAdapterHorizontal(this)
        finishedEventAdapter = ListEventFinishedAdapter(this)

        // Set up RecyclerView for ongoing events (horizontal)
        binding.rvHorizontalHome.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = ongoingEventAdapter
        }

        // Set up RecyclerView for finished events (vertical)
        binding.rvVerticalHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedEventAdapter
        }

        // Observe ongoing events data
        homeViewModel.listOngoingEvents.observe(viewLifecycleOwner) { events ->
            ongoingEventAdapter.submitList(events)
        }

        // Observe finished events data
        homeViewModel.listFinishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        // Observe loading state
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors
        homeViewModel.isError.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Display error message or take necessary action
                // binding.errorTextView.text = it
            }
        }

        imageProfile = binding.ivProfile
        Glide.with(this)
            .load(imageProfileUrl)
            .into(imageProfile)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFinishedItemClickListener(event: ListEventsItem) {
        //TODO("Not yet implemented")
        val actionFinishedDetail = HomeFragmentDirections.actionNavigationHomeToDetailFinishedEventFragment()
        findNavController().navigate(actionFinishedDetail)
    }

    override fun onOngoingItemClickListener(event: ListEventsItem) {
        //TODO("Not yet implemented")
        val actionOngoing = HomeFragmentDirections.actionNavigationHomeToDetailOngoingEventFragment()
        findNavController().navigate(actionOngoing)
    }
}
