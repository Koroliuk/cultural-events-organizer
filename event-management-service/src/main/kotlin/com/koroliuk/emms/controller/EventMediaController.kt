package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.response.GetEventMediaResponse
import com.koroliuk.emms.model.event.EventMedia
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.EventMediaService
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.io.File
import java.io.FileOutputStream
import java.security.Principal


@Controller("/api/event/{eventId}/media")
class EventMediaController(
    @Inject private val eventMediaService: EventMediaService,
    @Inject private val eventService: EventService
) {

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get
    fun getMediaByEventId(
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE_SIZE) size: Int,
        eventId: Long
    ): GetEventMediaResponse {
        return eventMediaService.findByEventId(eventId, page, size)
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/{mediaId}/url")
    fun getMediaUrl(@PathVariable mediaId: Long, @PathVariable eventId: String): HttpResponse<Any> {
        val media = eventMediaService.findById(mediaId)
            ?: return HttpResponse.notFound(createMessageResponse("There is no media file with such id"))
        return HttpResponse.ok(eventMediaService.getPresignedUrl(media))
    }

    @Secured(USER)
    @Post(consumes = [MediaType.MULTIPART_FORM_DATA], produces = [MediaType.TEXT_PLAIN])
    fun uploadMedia(
        @PathVariable eventId: Long, @Part("file") file: CompletedFileUpload,
        principal: Principal
    ): HttpResponse<Any> {
        if (!ControllerUtils.isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        eventMediaService.addMediaToEvent(eventId, file)
        return HttpResponse.ok(createMessageResponse("File uploaded successfully"))
    }

    @Secured(USER)
    @Delete("/{mediaId}")
    fun deleteMedia(@PathVariable mediaId: Long, @PathVariable eventId: Long, principal: Principal): HttpResponse<Any> {
        if (!ControllerUtils.isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val media = eventMediaService.findById(mediaId)
            ?: return HttpResponse.notFound(createMessageResponse("There is no media file with such id"))
        eventMediaService.delete(media)
        return HttpResponse.noContent()
    }


}