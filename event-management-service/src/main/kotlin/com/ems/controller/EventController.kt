package com.ems.controller

import com.ems.dto.EventDto
import com.ems.dto.PurchaseRequest
import com.ems.model.Event
import com.ems.model.EventMedia
import com.ems.model.EventType
import com.ems.service.*
import com.ems.service.impl.S3Service
import com.ems.utils.MappingUtils
import io.micronaut.core.convert.format.Format
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
import java.time.LocalDateTime

@Controller("/api/events")
@Secured(SecurityRule.IS_AUTHENTICATED)
class EventController(
    @Inject private val eventService: EventService,
    @Inject private val ticketService: TicketService,
    @Inject private val userService: UserService,
    @Inject private val eventCategoryService: EventCategoryService,
    @Inject private val s3Service: S3Service,
    @Inject private val eventMediaService: EventMediaService,
    @Inject private val discountCodeService: DiscountCodeService
) {

    @Post
    @Secured("USER")
    fun create(@Body eventDto: EventDto, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)
        if (user != null) {
            if (user.blocked) {
                return HttpResponse.status(HttpStatus.FORBIDDEN)
            }
            if (eventDto.startTime > eventDto.endTime) {
                throw IllegalArgumentException("Start date must be earlier than end date")
            }
            if (EventType.ONLINE == eventDto.eventType && eventDto.url == null) {
                throw IllegalArgumentException("Url must be filled for online event")
            }
            if (EventType.OFFLINE == eventDto.eventType && eventDto.location == null) {
                throw IllegalArgumentException("Location must be filled for offline event")
            }
            val category = eventCategoryService.findByName(eventDto.category)
            if (category == null) {
                throw IllegalArgumentException("No such category")
            }
            val event = MappingUtils.convertToEntity(eventDto, category, user)
            return HttpResponse.created(eventService.create(event))
        }
        return HttpResponse.badRequest()
    }


    @Put("/{id}")
    @Secured("USER")
    fun update(@PathVariable id: Long, @Body eventDto: EventDto, principal: Principal): Event {
        if (!eventService.existById(id)) {
            throw IllegalArgumentException("No event with a such id")
        }
        val category = eventCategoryService.findByName(eventDto.category)
        if (category == null) {
            throw IllegalArgumentException("No such category")
        }
        val user = userService.findByUsername(principal.name)
        val event = MappingUtils.convertToEntity(eventDto, category, user!!)
        event.id = id
        return eventService.update(event)
    }

    @Get("/{id}")
    @Secured("USER")
    fun findById(@PathVariable id: Long): Event {
        return eventService.findById(id)
    }

    @Get
    @Secured("USER")
    fun findAll(): MutableIterable<Event> = eventService.findAll()

    @Get("/search")
    @Secured("USER")
    fun searchEvents(@QueryValue @Format("yyyy-MM-dd'T'HH:mm:ss'Z'") dateFrom: LocalDateTime?,
                     @QueryValue @Format("yyyy-MM-dd'T'HH:mm:ss'Z'") dateTo: LocalDateTime?,
                     @QueryValue keywords: List<String>?,
                     @QueryValue categories: List<String>?): MutableIterable<Event> {
        return eventService.searchEvents(keywords, categories, dateFrom, dateTo)
    }

    @Delete("/{id}")
    @Secured("USER", "ADMIN")
    fun deleteById(id: Long): HttpResponse<Any> {
        //todo: delete tickets also
        eventService.deleteById(id)
        return HttpResponse.status(HttpStatus.NO_CONTENT)
    }

    @Post("{eventId}/tickets")
    @Secured("USER")
    fun purchaseTicket(@PathVariable eventId: Long, @Body purchaseRequest: PurchaseRequest,
                       principal: Principal): HttpResponse<Any> {
        if (!eventService.existById(eventId)) {
            throw java.lang.IllegalArgumentException("There is no such event")
        }
        val amount = purchaseRequest.amount
        val event = eventService.findById(eventId)
        val user = userService.findByUsername(principal.name)
        if (user != null) {
            if (user.blocked) {
                return HttpResponse.status(HttpStatus.FORBIDDEN)
            }
            val discountCode = if (purchaseRequest.discountCode != null) {
                discountCodeService.findByCode(purchaseRequest.discountCode)
                    ?: return HttpResponse.status(HttpStatus.BAD_REQUEST, "Invalid discount code")
            } else null
            ticketService.purchaseTickets(event, user, amount, discountCode, purchaseRequest.isUnSubscribeFromWaitingList)
            return HttpResponse.ok()
        }
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Post("/{eventId}/media", consumes = [MediaType.MULTIPART_FORM_DATA], produces = [MediaType.TEXT_PLAIN])
    fun uploadMedia(@PathVariable eventId: Long, @Part("file") file: CompletedFileUpload): HttpResponse<Any> {
        val event = eventService.findById(eventId)

        val tempFile = File.createTempFile(file.filename, ".tmp")
        tempFile.deleteOnExit()

        FileOutputStream(tempFile).use { output ->
            output.write(file.bytes)
        }

        val s3Key = "events/$eventId/media/${file.filename}"

        s3Service.uploadFile(tempFile, s3Key)
        val eventMedia = EventMedia(s3Key = s3Key, event = event)
        eventMediaService.addMediaToEvent(eventMedia)

        return HttpResponse.ok(mapOf("message" to "File uploaded successfully", "s3Key" to s3Key))
    }

    @Get("/{eventId}/media")
    fun getMediaByEventId(eventId: Long): List<EventMedia> {
        return eventMediaService.findByEventId(eventId)
    }

    @Get("/media/{mediaId}/url")
    fun getMediaUrl(@PathVariable mediaId: Long): HttpResponse<Any> {
        val media = eventMediaService.findById(mediaId)
        val presignedUrl = s3Service.generatePresignedUrl(media.s3Key)
        return HttpResponse.ok(mapOf("url" to presignedUrl))
    }

    @Delete("/media/{mediaId}")
    fun deleteMedia(@PathVariable mediaId: Long): HttpResponse<Any> {
        val media = eventMediaService.findById(mediaId)
        s3Service.deleteFile(media.s3Key)
        eventMediaService.deleteById(mediaId)
        return HttpResponse.noContent()
    }

}
