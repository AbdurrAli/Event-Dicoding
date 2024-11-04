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
import de.hdodenhof.circleimageview.CircleImageView
import com.example.dicodingevent.adapter.ListEventOngoingAdapterVertical
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentOnGoingBinding
import com.example.dicodingevent.ui.viewmodel.OnGoingViewModel

class OnGoingFragment : Fragment(), ListEventOngoingAdapterVertical.OnOngoingItemClickListener {

    private var _binding: FragmentOnGoingBinding? = null
    private val binding get() = _binding!!

    private val ongoingViewModel: OnGoingViewModel by viewModels()
    private lateinit var ongoingEventAdapter: ListEventOngoingAdapterVertical

    private lateinit var imageProfile: CircleImageView
    private var imageProfileUrl = "https://w.wallhaven.cc/full/zy/wallhaven-zy759g.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnGoingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize adapters
        ongoingEventAdapter = ListEventOngoingAdapterVertical(this)


        // Set up RecyclerView for ongoing events (horizontal)
        binding.rvVerticalHome.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ongoingEventAdapter
        }


        // Observe ongoing events data
        ongoingViewModel.listOngoingEvents.observe(viewLifecycleOwner) { events ->
            ongoingEventAdapter.submitList(events)
        }

        // Observe loading state
        ongoingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors
        ongoingViewModel.isError.observe(viewLifecycleOwner) { error ->
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

    override fun onOngoingItemClickListener(event: ListEventsItem) {
        //TODO("Not yet implemented")
        val actionOngoing = OnGoingFragmentDirections.actionNavigationUpcomingToDetailOngoingEventFragment()
        findNavController().navigate(actionOngoing)
    }
}
