package com.example.dicodingevent.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.dicodingevent.databinding.FragmentDetailOngoingEventBinding
import com.example.dicodingevent.ui.viewmodel.DetailOngoingEventViewModel
import com.example.dicodingevent.ui.viewmodel.NetworkStatusViewModel
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale

class DetailOngoingEventFragment : Fragment() {

    private lateinit var viewModel: DetailOngoingEventViewModel
    private var _binding: FragmentDetailOngoingEventBinding? = null
    private val binding get() = _binding!!

    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    private val args: DetailOngoingEventFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailOngoingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[DetailOngoingEventViewModel::class.java]

        // Set up the toolbar
        setupToolbar()

        val eventId = args.eventId

        viewModel.getEventDetail(eventId)

        networkStatusViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            Log.d("NetworkStatus", "Is Connected: $isConnected")
            if (!isConnected) {
                val actionNetworkStatus = DetailOngoingEventFragmentDirections.actionDetailOngoingEventFragmentToNetworkStatusFragment()
                findNavController().navigate(actionNetworkStatus)
            }
        }

        // Observe LiveData for selected event details
        viewModel.selectedEvent.observe(viewLifecycleOwner) { event ->
            event?.let { selectedEvent ->
                Log.d("API Response", "Event Name: ${event.name}, City: ${event.cityName}")
                // Bind detail event to UI
                binding.tvEventTitle.text = selectedEvent.name ?: "Nama tidak tersedia"
                binding.tvPlace.text = selectedEvent.cityName ?: "Lokasi tidak tersedia"
                binding.tvEventCategory.text = selectedEvent.category ?: "Kategori tidak tersedia"
                binding.tvEventProvider.text = selectedEvent.ownerName ?: "Penyedia tidak tersedia"
                binding.tvContentEventSummary.text = selectedEvent.summary ?: "Ringkasan tidak tersedia"

                // Format date and time
                binding.tvDate.text = formatDate(selectedEvent.beginTime)

                // Load image using Glide
                Glide.with(this)
                    .load(selectedEvent.imageLogo)
                    .into(binding.ivImgEvent)

                // Set up button click listener to open URL
                binding.btnRegister.setOnClickListener {
                    val url = selectedEvent.link
                    if (!url.isNullOrEmpty()) {
                        openWebPage(url)
                    }
                }

                // Parse HTML content for description using Jsoup
                val htmlContent = selectedEvent.description ?: "Deskripsi tidak tersedia"
                val document = Jsoup.parse(htmlContent)
                val textContent = document.body().text() // Extract clean text
                binding.tvContentEventDescription.text = Html.fromHtml(textContent, Html.FROM_HTML_MODE_COMPACT)
            }
        }

        // Observe remaining quota
        viewModel.remainingQuota.observe(viewLifecycleOwner) {
            binding.tvRegistrant.text = it.toString()
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun setupToolbar() {
        // Set the toolbar as the ActionBar
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Remove the default title
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set a navigation click listener to go back when the back icon is pressed
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun formatDate(dateString: String?): String {
        return if (dateString != null) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: "")
            } catch (e: Exception) {
                dateString // Return original if formatting fails
            }
        } else {
            "Date unavailable"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
