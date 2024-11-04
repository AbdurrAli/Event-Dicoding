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
import com.example.dicodingevent.adapter.ListEventFinishedAdapter
import de.hdodenhof.circleimageview.CircleImageView
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.ui.viewmodel.FinishedViewModel

class FinishedFragment : Fragment(), ListEventFinishedAdapter.OnFinishedItemClickListener {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private val ongoingViewModel: FinishedViewModel by viewModels()
    private lateinit var finishedEventAdapter: ListEventFinishedAdapter

    private lateinit var imageProfile: CircleImageView
    private var imageProfileUrl = "https://w.wallhaven.cc/full/zy/wallhaven-zy759g.png"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize adapters
        finishedEventAdapter = ListEventFinishedAdapter(this)


        // Set up RecyclerView for ongoing events (horizontal)
        binding.rvVerticalHome.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = finishedEventAdapter
        }


        // Observe ongoing events data
        ongoingViewModel.listFinishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
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

    override fun onFinishedItemClickListener(event: ListEventsItem) {
        // TODO("Not yet implemented")
        val actionFinished =
            FinishedFragmentDirections.actionNavigationFinishedToDetailFinishedEventFragment()
        findNavController().navigate(actionFinished)
    }
}
