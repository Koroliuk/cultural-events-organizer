package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventType
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import java.time.LocalDateTime


@Repository
interface EventRepository : JpaRepository<Event, Long> {

    @Query("""
    SELECT * FROM events e 
    LEFT JOIN event_categories ec ON e.category_id = ec.id 
    LEFT JOIN online_events oe ON e.id = oe.event_id 
    LEFT JOIN offline_events offe ON e.id = offe.event_id
    WHERE
        ((:keywords IS NULL) OR (LOWER(e.name) LIKE LOWER(CONCAT('%', :keywords, '%'))))
        AND ((:categories IS NULL) OR (ec.name IN (:categories)))
        AND (((CAST(:startTime AS timestamp)) IS NULL) OR (e.start_time >= CAST(:startTime AS timestamp)))
        AND (((CAST(:endTime AS timestamp)) IS NULL) OR (e.end_time >= CAST(:endTime AS timestamp)))
        AND ((:location IS NULL) OR (offe.location ILIKE ('%' || :location || '%')))
        AND ((:eventType IS NULL) OR ((:eventType = 'ONLINE' AND oe.event_id IS NOT NULL) OR (:eventType = 'OFFLINE' AND offe.event_id IS NOT NULL)))
""", nativeQuery = true)
    fun searchEvents(
        keywords: String?,
        categories: List<String>?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        location: String?,
        eventType: EventType?
    ): List<Event>



//    @Query("""
//        SELECT * FROM events e
//        LEFT JOIN event_categories ec ON e.category_id = ec.id
//        LEFT JOIN online_events oe ON e.id = oe.id
//        LEFT JOIN offline_events offe ON e.id = offe.id
//        WHERE (:location IS NULL OR offe.location ILIKE ('%' || :location || '%'))
//            AND (:eventType IS NULL OR (:eventType = 'ONLINE' AND oe.id IS NOT NULL) OR (:eventType = 'OFFLINE' AND offe.id IS NOT NULL))
//        ORDER BY
//        CASE WHEN :sortField = 'name' and :sortDirection = 'asc' THEN e.name END ASC ,
//        CASE WHEN :sortField = 'name' and :sortDirection = 'desc' THEN e.name END DESC ,
//        CASE WHEN :sortField = 'startTime' and :sortDirection = 'asc' THEN e.start_time END ASC ,
//        CASE WHEN :sortField = 'startTime' and :sortDirection = 'desc' THEN e.start_time END DESC ,
//        CASE WHEN :sortField = 'endTime' and :sortDirection = 'asc' THEN e.end_time END ASC ,
//        CASE WHEN :sortField = 'endTime' and :sortDirection = 'desc' THEN e.end_time END DESC
//        LIMIT :pageSize OFFSET :pageStart
//    """, nativeQuery = true, countQuery = """
//        SELECT COUNT(*) FROM events e
//        LEFT JOIN event_categories ec ON e.category_id = ec.id
//        LEFT JOIN online_events oe ON e.id = oe.id
//        LEFT JOIN offline_events offe ON e.id = offe.id
//        WHERE (:location IS NULL OR offe.location ILIKE ('%' || :location || '%'))
//            AND (:eventType IS NULL OR (:eventType = 'ONLINE' AND oe.id IS NOT NULL) OR (:eventType = 'OFFLINE' AND offe.id IS NOT NULL))
//        ORDER BY
//        CASE WHEN :sortField = 'name' and :sortDirection = 'asc' THEN e.name END ASC ,
//        CASE WHEN :sortField = 'name' and :sortDirection = 'desc' THEN e.name END DESC ,
//        CASE WHEN :sortField = 'startTime' and :sortDirection = 'asc' THEN e.start_time END ASC ,
//        CASE WHEN :sortField = 'startTime' and :sortDirection = 'desc' THEN e.start_time END DESC ,
//        CASE WHEN :sortField = 'endTime' and :sortDirection = 'asc' THEN e.end_time END ASC ,
//        CASE WHEN :sortField = 'endTime' and :sortDirection = 'desc' THEN e.end_time END DESC
//        LIMIT :pageSize OFFSET :pageStart
//    """)
//    fun searchEvents(
//        keywords: String?,
//        categories: List<String>?,
//        startTime: LocalDateTime?,
//        endTime: LocalDateTime?,
//        location: String?,
//        eventType: EventType?,
//        sortField: String,
//        sortDirection: String?,
//        pageable: Pageable
//    ): Page<Event>


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

}
