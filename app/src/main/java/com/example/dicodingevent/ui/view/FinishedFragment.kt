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
import com.example.dicodingevent.adapter.ListEventFinishedAdapter
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.ui.viewmodel.FinishedViewModel
import com.example.dicodingevent.ui.viewmodel.NetworkStatusViewModel

class FinishedFragment : Fragment(), ListEventFinishedAdapter.OnFinishedItemClickListener {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private val finishedViewModel: FinishedViewModel by viewModels()
    private lateinit var finishedEventAdapter: ListEventFinishedAdapter

    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    private lateinit var imageProfile: CircleImageView
    private var imageProfileUrl = "https://w.wallhaven.cc/full/zy/wallhaven-zy759g.png"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observasi status jaringan
        networkStatusViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            Log.d("NetworkStatus", "Is Connected: $isConnected")
            if (!isConnected) {
                val actionNetworkStatus = FinishedFragmentDirections.actionNavigationFinishedToNetworkStatusFragment()
                findNavController().navigate(actionNetworkStatus)
            }
        }

        // Inisialisasi adapter
        finishedEventAdapter = ListEventFinishedAdapter(this)

        // Setup RecyclerView untuk event selesai (vertikal)
        binding.rvVerticalHome.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = finishedEventAdapter
        }

        // Observasi data event selesai
        finishedViewModel.listFinishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        // Observasi status loading
        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi error
        finishedViewModel.isError.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Tampilkan pesan error jika diperlukan
            }
        }

        // Atur gambar profil menggunakan Glide
        imageProfile = binding.ivProfile
        Glide.with(this)
            .load(imageProfileUrl)
            .into(imageProfile)

        // Swipe-to-refresh
        binding.swipeRefresh.setOnRefreshListener {
            finishedViewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFinishedItemClickListener(event: ListEventsItem) {
        // Arahkan ke fragment detail dengan SafeArgs
        val actionFinished = FinishedFragmentDirections
            .actionNavigationFinishedToDetailFinishedEventFragment()
        findNavController().navigate(actionFinished)
    }
}
