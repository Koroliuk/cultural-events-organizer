package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.request.SeatDto
import com.koroliuk.emms.controller.request.SeatUpdateDto
import com.koroliuk.emms.controller.response.GetSeatsResponse
import com.koroliuk.emms.model.attendance.PriceCategory
import com.koroliuk.emms.model.attendance.Seat
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.repository.attendance.SeatRepository
import com.koroliuk.emms.service.SeatService
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SeatServiceImpl(
    @Inject val seatRepository: SeatRepository
): SeatService {

    override fun getAllByEventId(eventId: Long, pageable: Pageable): GetSeatsResponse {
        val result = seatRepository.findAllByEventId(eventId, pageable)
        return GetSeatsResponse(
            page = result.pageable.number,
            size = result.pageable.size,
            totalSize = result.totalSize,
            totalPage = result.totalPages,
            content = result.content.stream()
                .map { seatToDto(it) }
                .toList()
        )
    }

    override fun getById(id: Long): SeatDto? {
        return seatRepository.findById(id)
            .map { seatToDto(it) }
            .orElse(null)
    }

    override fun delete(id: Long) {
        seatRepository.deleteById(id)
    }

    override fun findByEvent(event: Event): List<Seat> {
        return seatRepository.findAllByEventId(event.id!!)
    }

    override fun create(seatRequest: SeatDto, event: Event, priceCategory: PriceCategory): SeatDto {
        val seat = Seat(seatRequest.number, event, priceCategory)
        return seatToDto(seatRepository.save(seat))
    }

    override fun update(seat: Seat, seatDto: SeatUpdateDto, priceCategory: PriceCategory?): SeatDto {
        if (seatDto.number != null) {
            seat.number = seatDto.number
        }
        if (priceCategory != null) {
            seat.priceCategory = priceCategory
        }
        return seatToDto(seatRepository.update(seat))
    }

    override fun findById(id: Long): Seat? {
        return seatRepository.findById(id).orElse(null)
    }

    override fun existsById(id: Long): Boolean {
        return seatRepository.existsById(id)
    }

    private fun seatToDto(seat: Seat): SeatDto {
        return SeatDto(
            id = seat.id!!,
            priceCategoryId = seat.priceCategory.id!!,
            eventId = seat.event.id!!,
            number = seat.number
        )
    }
 }
