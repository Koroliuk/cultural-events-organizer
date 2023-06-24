package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.EventMediaPresignedUrl
import com.koroliuk.emms.controller.response.GetEventMediaResponse
import com.koroliuk.emms.model.event.EventMedia
import io.micronaut.http.multipart.CompletedFileUpload

interface EventMediaService {

    fun addMediaToEvent(eventId: Long, file: CompletedFileUpload)

    fun findByEventId(id: Long): List<EventMedia>

    fun findByEventId(id: Long, page: Int, size: Int): GetEventMediaResponse

    fun findById(id: Long): EventMedia?

    fun delete(media: EventMedia)

    fun getPresignedUrl(media: EventMedia): EventMediaPresignedUrl

}