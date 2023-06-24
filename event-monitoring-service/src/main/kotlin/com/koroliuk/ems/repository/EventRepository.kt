package com.koroliuk.ems.repository

import com.koroliuk.ems.model.Event
import com.koroliuk.ems.model.NotificationInfo
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import javax.persistence.Tuple

@Repository
interface EventRepository : CrudRepository<Event, Long> {

    @Query("""
        SELECT a.user_id as userId, 
        COALESCE(s.event_id, pc.event_id) as eventId,
        eo.group_id as groupID
        FROM attendance_entries a 
        LEFT JOIN attendance_entry_with_limited_seats aewls on a.id = aewls.attendance_entry_id
        LEFT JOIN attendance_entry_with_unlimited_seats aewus on a.id = aewus.attendance_entry_id
        LEFT JOIN price_categories pc on aewus.price_category_id = pc.id
        LEFT JOIN seat s on aewls.seat_id = s.id
        LEFT JOIN events e on COALESCE(s.event_id, pc.event_id) = e.id
        JOIN event_organizers eo on e.id = eo.event_id
        WHERE e.end_time < now()
        ORDER BY user_id LIMIT :batchSize
    """, nativeQuery = true)
    fun findEndedEvents(batchSize: Int): List<NotificationInfo>

    @Query("""
        SELECT a.user_id as userId, 
        COALESCE(s.event_id, pc.event_id) as eventId,
        eo.group_id as groupID
        FROM attendance_entries a 
        LEFT JOIN attendance_entry_with_limited_seats aewls on a.id = aewls.attendance_entry_id
        LEFT JOIN attendance_entry_with_unlimited_seats aewus on a.id = aewus.attendance_entry_id
        LEFT JOIN price_categories pc on aewus.price_category_id = pc.id
        LEFT JOIN seat s on aewls.seat_id = s.id
        LEFT JOIN events e on COALESCE(s.event_id, pc.event_id) = e.id
        JOIN event_organizers eo on e.id = eo.event_id         
        WHERE start_time BETWEEN now() AND now() + INTERVAL '30 minute'
    """, nativeQuery = true)
    fun findStartingEvents(batchSize: Int): List<NotificationInfo>

}
