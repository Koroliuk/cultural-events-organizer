package com.ems.repository

import com.ems.model.Event
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime


@Repository
interface EventRepository : CrudRepository<Event, Long> {

    @Query("""
        SELECT e.* FROM events e
        JOIN event_categories ec ON e.category_id = ec.id
        WHERE ((CAST(:keywords AS text)) IS NULL
        OR LOWER(e.name) SIMILAR TO LOWER(:keywords)
        OR LOWER(e.description) SIMILAR TO LOWER(:keywords)
        OR LOWER(e.location) SIMILAR TO LOWER(:keywords))
        AND (CAST(:timeFrom AS timestamp) IS NULL OR e.start_time >= CAST(:timeFrom AS timestamp))
        AND (CAST(:timeTo AS timestamp) IS NULL OR e.start_time <= CAST(:timeTo AS timestamp))
        AND (:categories IS NULL OR LOWER(ec.name) = ANY(ARRAY[:categories]))
    """, nativeQuery = true)
    fun searchEventsByStartTimeBetweenAndKeywords(timeFrom: LocalDateTime?, timeTo: LocalDateTime?, keywords: String?, categories: List<String>?): MutableIterable<Event>

    @Query("""
        SELECT e.* FROM events e
        JOIN event_categories ec ON e.category_id = ec.id
        WHERE ((CAST(:timeFrom AS timestamp) IS NULL AND CAST(:timeTo AS timestamp) IS NULL) 
        OR (e.start_time >= CAST(:timeFrom AS timestamp) AND CAST(:timeTo AS timestamp) IS NULL) 
        OR (CAST(:timeFrom AS timestamp) IS NULL AND e.start_time <= CAST(:timeTo AS timestamp)) 
        OR (e.start_time BETWEEN CAST(:timeFrom AS timestamp) AND CAST(:timeTo AS timestamp)))
        AND (:categories IS NULL OR LOWER(ec.name) = ANY(ARRAY[:categories]))
    """, nativeQuery = true)
    fun searchByStartTimeBetween(timeFrom: LocalDateTime?, timeTo: LocalDateTime?, categories: List<String>?): MutableIterable<Event>

    @Query("""
        SELECT * FROM events e 
        JOIN event_creators ec ON e.id = ec.event_id
        JOIN users u ON u.id = ec.user_id
        WHERE u.username = :username
    """, nativeQuery = true)
    fun findByCreatorUsername(username: String): List<Event>

    fun findByInvitationCode(code: String): Event?

}
