package com.example.dicodingevent.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.databinding.CardVerticalBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ListEventFinishedAdapter(private val listener: OnFinishedItemClickListener) : RecyclerView.Adapter<ListEventFinishedAdapter.EventViewHolder>() {

    private var eventList: List<ListEventsItem> = listOf()

    interface OnFinishedItemClickListener {
        fun onFinishedItemClickListener(event: ListEventsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.bind(event)


    }

    override fun getItemCount(): Int = eventList.size

    fun submitList(events: List<ListEventsItem>) {
        eventList = events
        notifyDataSetChanged()
    }

    inner class EventViewHolder(private val binding: CardVerticalBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            // Bind your data here
            binding.titleEvent.text = event.name // Adjust according to your data structure
            binding.tvOwner.text = event.ownerName // Adjust according to your data structure
            binding.dateEvent.text = event.beginTime // Format the date as needed

            binding.dateEvent.text = event.beginTime?.let { formatDateTime(it) }

            // Load image using Glide or another library
            Glide.with(binding.imageView.context)
                .load(event.imageLogo) // Adjust to the correct image URL
                .into(binding.imageView)

            binding.root.setOnClickListener{
                listener.onFinishedItemClickListener(event)
            }

        }

    }

    // Function to format `beginTime`
    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            return date?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
            dateTime // fallback to original if parsing fails
        }
    }
}
