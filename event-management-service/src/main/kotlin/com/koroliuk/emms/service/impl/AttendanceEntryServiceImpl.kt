package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.AttendanceEntryResponse
import com.koroliuk.emms.controller.response.GetAttendanceEntryResponse
import com.koroliuk.emms.model.attendance.*
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.notification.NotificationType
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.repository.attendance.AttendanceEntryWithLimitedSeatsRepository
import com.koroliuk.emms.repository.attendance.AttendanceEntryWithUnlimitedSeatsRepository
import com.koroliuk.emms.repository.attendance.AttendanceEntryRepository
import com.koroliuk.emms.service.NotificationService
import com.koroliuk.emms.service.AttendanceEntryService
import com.koroliuk.emms.service.SeatService
import com.koroliuk.emms.service.WaitListService
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.transaction.Transactional


@Singleton
open class AttendanceEntryServiceImpl(
    @Inject private val attendanceEntryRepository: AttendanceEntryRepository,
    @Inject private val attendanceEntryWithLimitedSeatsRepository: AttendanceEntryWithLimitedSeatsRepository,
    @Inject private val attendanceEntryWithUnlimitedSeatsRepository: AttendanceEntryWithUnlimitedSeatsRepository,
    @Inject private val waitListService: WaitListService,
    @Inject private val seatsService: SeatService,
    @Inject private val notificationService: NotificationService

) : AttendanceEntryService {
    override fun update(attendanceEntry: AttendanceEntry) {
        attendanceEntryRepository.update(attendanceEntry)
    }

    @Transactional
    override fun purchaseTicket(
            event: Event,
            user: User,
            seat: Seat?,
            priceCategory: PriceCategory?,
            discountCode: DiscountCode?,
            isUnSubscribeFromWaitingList: Boolean
    ) {
        if (priceCategory != null) {
            val price = if (discountCode != null) {
                calculateDiscountedPrice(priceCategory.price, discountCode)
            } else {
                priceCategory.price
            }
            val attend = AttendanceEntry(
                user = user,
                purchaseTime = LocalDateTime.now(),
                status = AttendanceEntryStatus.ACTIVE,
                priceToPay = price
            )
            val saved = attendanceEntryRepository.save(attend)
            val attendUnlimit = AttendanceEntryWithUnlimitedSeats(
                attendanceEntry = saved,
                priceCategory = priceCategory
            )
            attendanceEntryWithUnlimitedSeatsRepository.save(attendUnlimit)
        }
        if (seat != null) {
            if (seat.isTaken) {
                throw IllegalArgumentException("OSeat taken")
            }
            val price = if (discountCode != null) {
                calculateDiscountedPrice(seat.priceCategory.price, discountCode)
            } else {
                seat.priceCategory.price
            }
            val attend = AttendanceEntry(
                user = user,
                purchaseTime = LocalDateTime.now(),
                status = AttendanceEntryStatus.ACTIVE,
                priceToPay = price
            )
            val saved = attendanceEntryRepository.save(attend)
            val attLimited = AttendanceEntryWithLimitedSeats(
                attendanceEntry = saved,
                seat = seat
            )
            attendanceEntryWithLimitedSeatsRepository.save(attLimited)
        }

        if (isUnSubscribeFromWaitingList) {
            waitListService.deleteByEventAndUser(event, user)
        }
    }

    override fun findById(id: Long): AttendanceEntry? {
        return attendanceEntryRepository.findById(id).orElse(null)
    }

    private fun eventIsSeatsLimited(event: Event): Boolean {
        return seatsService.findByEvent(event).isNotEmpty()
    }

    private fun calculateDiscountedPrice(originalPrice: BigDecimal, discountCode: DiscountCode): BigDecimal {
        if (discountCode.expirationDate < LocalDateTime.now()) {
            return originalPrice
        }
        return if (discountCode.type == DiscountType.PERCENTAGE) {
            val discount = 1 - discountCode.value / 100.0
            originalPrice * BigDecimal.valueOf(discount)
        } else {
            (originalPrice - BigDecimal.valueOf(discountCode.value)).coerceAtLeast(BigDecimal.ZERO)
        }
    }

    override fun findPurchasedTicketsByUserId(userId: Long): List<AttendanceEntry> {
        return attendanceEntryRepository.findByUserId(userId)
    }

    override fun findPurchasedTicketsByUserIdPaginated(userId: Long, page: Int, size: Int): GetAttendanceEntryResponse {
        val pageable = Pageable.from(page, size)
        val attendanceEntriesPage = attendanceEntryRepository.findByUserId(userId, pageable)
        return GetAttendanceEntryResponse(
            page = attendanceEntriesPage.pageable.number,
            size = attendanceEntriesPage.size,
            totalSize = attendanceEntriesPage.totalSize,
            totalPage = attendanceEntriesPage.totalPages,
            content = attendanceEntriesPage.content.stream()
                .map {
                    var seatNumber: Long? = null
                    var eventId: Long? = null
                    if (it.attendanceEntryWithLimitedSeats != null) {
                        val seat = it.attendanceEntryWithLimitedSeats!!.seat
                        seatNumber = seat.id
                        eventId = seat.event.id
                    } else if (it.attendanceEntryWithUnlimitedSeats != null) {
                        eventId = it.attendanceEntryWithUnlimitedSeats!!.priceCategory.event.id
                    }
                    AttendanceEntryResponse(
                        purchaseTime = it.purchaseTime,
                        status = it.status,
                        priceToPay = it.priceToPay,
                        eventId = eventId!!,
                        seatNumber = seatNumber
                    )
                }
                .toList()
        )
    }

    override fun findUsersByEvent(event: Event): List<User> {
        val attendanceEntryLimited = attendanceEntryWithLimitedSeatsRepository.findAllBySeatEvent(event)
        val attendanceEntryUnlimited = attendanceEntryWithUnlimitedSeatsRepository.findAllByPriceCategoryEvent(event)

        val activeLimitedUsers = attendanceEntryLimited
            .filter { it.attendanceEntry.status == AttendanceEntryStatus.ACTIVE }
            .map { it.attendanceEntry.user }

        val activeUnlimitedUsers = attendanceEntryUnlimited
            .filter { it.attendanceEntry.status == AttendanceEntryStatus.ACTIVE }
            .map { it.attendanceEntry.user }

        return activeLimitedUsers + activeUnlimitedUsers
    }


    override fun isUserAttendEvent(event: Event, user: User): Boolean {
        return findPurchasedTicketsByUserId(user.id!!).stream()
                .filter { it.status == AttendanceEntryStatus.ACTIVE }
                .map {
                    if (it.attendanceEntryWithUnlimitedSeats != null) {
                        return@map it.attendanceEntryWithUnlimitedSeats!!.priceCategory.event
                    }
                    return@map it.attendanceEntryWithLimitedSeats!!.seat.event
                }
                .anyMatch { it.id == event.id }
    }

    override fun countByStatusAndEventId(status: AttendanceEntryStatus, eventId: Long): Long {
        return attendanceEntryRepository.findByStatus(status).stream()
                .filter{
                    if (it.attendanceEntryWithLimitedSeats != null) {
                        return@filter it.attendanceEntryWithLimitedSeats!!.seat.event.id == eventId
                    }
                    return@filter it.attendanceEntryWithUnlimitedSeats!!.priceCategory.event.id == eventId
                }
                .count()
    }

    override fun cancelById(id: Long) {
        val optionalTicket = attendanceEntryRepository.findById(id)
        if (optionalTicket.isEmpty) {
            throw IllegalArgumentException("No ticket with such id")
        }
        val ticket = optionalTicket.get()
        ticket.status = AttendanceEntryStatus.CANCELED
        val event = if (ticket.attendanceEntryWithLimitedSeats != null) {
            ticket.attendanceEntryWithLimitedSeats!!.seat.event
        } else {
            ticket.attendanceEntryWithUnlimitedSeats!!.priceCategory.event
        }
        if (eventIsSeatsLimited(event)) {
            waitListService.findByEvent(event).stream()
                .map { w -> w.user }
                .forEach { user -> notificationService.addNotificationForUser(user, "There is new tickets for this event", NotificationType.EVENT_NEW_TICKETS, event, null) }
        }
        attendanceEntryRepository.update(ticket)
    }

}
