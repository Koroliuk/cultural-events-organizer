package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.GetAttendanceEntryResponse
import com.koroliuk.emms.model.attendance.*
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User

interface AttendanceEntryService {

    fun update(attendanceEntry: AttendanceEntry)

    fun purchaseTicket(event: Event, user: User, seat: Seat?, priceCategory: PriceCategory?, discountCode: DiscountCode?,
                       isUnSubscribeFromWaitingList: Boolean)

    fun findById(id: Long): AttendanceEntry?

    fun findPurchasedTicketsByUserId(userId: Long) : List<AttendanceEntry>

    fun findPurchasedTicketsByUserIdPaginated(userId: Long, page: Int, size: Int) : GetAttendanceEntryResponse

    fun findUsersByEvent(event: Event): List<User>

    fun isUserAttendEvent(event: Event, user: User): Boolean

    fun countByStatusAndEventId(status: AttendanceEntryStatus, eventId: Long): Long

    fun cancelById(id: Long)

}
