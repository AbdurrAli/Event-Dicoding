package com.example.dicodingevent.data.response

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the overall response structure for event data.
 *
 * @property listEvents A list of individual event items or null if not present.
 * @property error Boolean indicating if there was an error with the response.
 * @property message A message detailing success or error information.
 */
data class EventResponse(

	@field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem?>? = null,

	@field:SerializedName("event")
	val event: ListEventsItem? = null, // Untuk menangani respons tunggal

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

/**
 * Data class representing individual event details.
 *
 * @property summary A brief summary or description of the event.
 * @property mediaCover URL of the cover image for the event.
 * @property registrants The number of people registered for the event.
 * @property imageLogo URL of the event's logo image.
 * @property link URL link to the event's website or further information.
 * @property description Detailed description of the event.
 * @property ownerName Name of the event organizer or owner.
 * @property cityName Name of the city where the event is held.
 * @property quota Maximum participant capacity for the event.
 * @property name The name/title of the event.
 * @property id Unique identifier for the event.
 * @property beginTime Event start time, formatted as "yyyy-MM-dd HH:mm:ss".
 * @property endTime Event end time, formatted as "yyyy-MM-dd HH:mm:ss".
 * @property category Category of the event, e.g., "Tech", "Business".
 */
data class ListEventsItem(

	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("mediaCover")
	val mediaCover: String? = null,

	@field:SerializedName("registrants")
	val registrants: Int? = null,

	@field:SerializedName("imageLogo")
	val imageLogo: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("ownerName")
	val ownerName: String? = null,

	@field:SerializedName("cityName")
	val cityName: String? = null,

	@field:SerializedName("quota")
	val quota: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("beginTime")
	val beginTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("category")
	val category: String? = null
)
