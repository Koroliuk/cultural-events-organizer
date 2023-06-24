package com.koroliuk.emms.service

import com.koroliuk.emms.controller.request.SeatDto
import com.koroliuk.emms.controller.request.SeatUpdateDto
import com.koroliuk.emms.controller.response.GetSeatsResponse
import com.koroliuk.emms.model.attendance.PriceCategory
import com.koroliuk.emms.model.attendance.Seat
import com.koroliuk.emms.model.event.Event
import io.micronaut.data.model.Pageable

interface SeatService {

    fun getAllByEventId(eventId: Long, pageable: Pageable): GetSeatsResponse

    fun getById(id: Long): SeatDto?

    fun delete(id: Long)

    fun findByEvent(event: Event): List<Seat>

    fun create(seatRequest: SeatDto, event: Event, priceCategory: PriceCategory) : SeatDto

    fun update(seat: Seat, seatDto: SeatUpdateDto, priceCategory: PriceCategory?): SeatDto

    fun findById(id: Long): Seat?

    fun existsById(id: Long): Boolean
}