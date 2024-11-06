package com.example.dicodingevent.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.CardVerticalBinding
import java.text.SimpleDateFormat
import java.util.Locale

// Adapter class for displaying ongoing events in a vertical list
class ListEventOngoingAdapterVertical(private val listener: OnOngoingItemClickListener) : RecyclerView.Adapter<ListEventOngoingAdapterVertical.EventViewHolder>() {

    private var eventList: List<ListEventsItem> = listOf() // List of ongoing events

    // Interface for handling item click events
    interface OnOngoingItemClickListener {
        fun onOngoingItemClickListener(event: ListEventsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        // Inflate the layout for each event item using CardVerticalBinding
        val binding = CardVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        // Bind data to the ViewHolder at the given position
        val event = eventList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = eventList.size

    // Updates the event list and refreshes the RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(events: List<ListEventsItem>) {
        eventList = events
        notifyDataSetChanged()
    }

    // ViewHolder class for each event item
    inner class EventViewHolder(private val binding: CardVerticalBinding) : RecyclerView.ViewHolder(binding.root) {

        // Bind event data to the UI components in the item layout
        fun bind(event: ListEventsItem) {
            binding.titleEvent.text = event.name // Set event name
            binding.tvOwner.text = event.ownerName // Set event owner's name

            // Format and set event start date, if available
            binding.dateEvent.text = event.beginTime?.let { formatDateTime(it) }

            // Load the event logo image using Glide
            Glide.with(binding.imageView.context)
                .load(event.imageLogo)
                .into(binding.imageView)

            // Set up click listener for the entire item
            binding.root.setOnClickListener {
                listener.onOngoingItemClickListener(event)
            }
        }
    }

    // Helper function to format `beginTime` to a more readable format
    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            date?.let { outputFormat.format(it) } ?: "" // Format if date parsing succeeds
        } catch (e: Exception) {
            dateTime // Return original string if parsing fails
        }
    }
}
