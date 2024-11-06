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
import com.bumptech.glide.Glide
import com.example.dicodingevent.databinding.FragmentDetailFinishedEventBinding
import com.example.dicodingevent.ui.viewmodel.DetailFinishedEventViewModel
import com.example.dicodingevent.ui.viewmodel.NetworkStatusViewModel
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale

class DetailFinishedEventFragment : Fragment() {

    private lateinit var viewModel: DetailFinishedEventViewModel
    private var _binding: FragmentDetailFinishedEventBinding? = null
    private val binding get() = _binding!!

    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[DetailFinishedEventViewModel::class.java]

        // Set up the toolbar
        setupToolbar()

        // Observe network status
        networkStatusViewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            Log.d("NetworkStatus", "Is Connected: $isConnected")
            if (!isConnected) {
                val actionNetworkStatus = DetailFinishedEventFragmentDirections.actionDetailFinishedEventFragmentToNetworkStatusFragment()
                findNavController().navigate(actionNetworkStatus)
            }
        }

        // Observe LiveData for selected event details
        viewModel.selectedEvent.observe(viewLifecycleOwner) { event ->
            event?.let {
                // Bind event details to UI
                binding.tvEventTitle.text = it.name ?: "Nama tidak tersedia"
                binding.tvPlace.text = it.cityName ?: "Lokasi tidak tersedia"
                binding.tvEventCategory.text = it.category ?: "Kategori tidak tersedia"
                binding.tvEventProvider.text = it.ownerName ?: "Penyedia tidak tersedia"
                binding.tvContentEventSummary.text = it.summary ?: "Ringkasan tidak tersedia"

                // Format date and time
                binding.tvDate.text = formatDate(it.beginTime)

                // Load image with placeholder using Glide
                Glide.with(this)
                    .load(it.imageLogo)
                    .into(binding.ivImgEvent)

                binding.btnRegister.setOnClickListener {
                    event.let { eventDetails ->
                        val url = eventDetails.link // Assuming 'link' is the URL field in your event data
                        if (url != null) {
                            if (url.isNotEmpty()) {
                                openWebPage(url)
                            }
                        }
                    }
                }

                // Parse HTML content for description using Jsoup
                val htmlContent = it.description ?: "Deskripsi tidak tersedia"
                val document = Jsoup.parse(htmlContent)
                val textContent = document.body().text()
                val descriptionText =
                    Html.fromHtml(textContent, Html.FROM_HTML_MODE_COMPACT)
                binding.tvContentEventDescription.text = descriptionText
            }
        }

        // Swipe refresh functionality
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }

        // Observe remaining quota
        viewModel.remainingQuota.observe(viewLifecycleOwner) {
            binding.tvRegistrant.text = it.toString()
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error state
        viewModel.isError.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun openWebPage(url: String?) {
        url?.let {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("DetailFinishedEvent", "Failed to open URL: $it", e)
            }
        }
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
            "Tanggal tidak tersedia"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
