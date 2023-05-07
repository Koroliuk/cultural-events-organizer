package com.ems.repository

import com.ems.model.Event
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime


@Repository
interface EventRepository : CrudRepository<Event, Long> {

    @Query("""
        SELECT * FROM events
        WHERE ((CAST(:keywords AS text)) IS NULL
        OR LOWER(name) SIMILAR TO LOWER(:keywords)
        OR LOWER(description) SIMILAR TO LOWER(:keywords)
        OR LOWER(location) SIMILAR TO LOWER(:keywords))
        AND (CAST(:timeFrom AS timestamp) IS NULL OR start_time >= CAST(:timeFrom AS timestamp))
        AND (CAST(:timeTo AS timestamp) IS NULL OR start_time <= CAST(:timeTo AS timestamp))
    """, nativeQuery = true)
    fun searchEventsByStartTimeBetweenAndKeywords(timeFrom: LocalDateTime?, timeTo: LocalDateTime?, keywords: String?): MutableIterable<Event>

    @Query("""
        SELECT * FROM events
        WHERE ((CAST(:timeFrom AS timestamp) IS NULL AND CAST(:timeTo AS timestamp) IS NULL) 
        OR (start_time >= CAST(:timeFrom AS timestamp) AND CAST(:timeTo AS timestamp) IS NULL) 
        OR (CAST(:timeFrom AS timestamp) IS NULL AND start_time <= CAST(:timeTo AS timestamp)) 
        OR (start_time BETWEEN CAST(:timeFrom AS timestamp) AND CAST(:timeTo AS timestamp)))
    """, nativeQuery = true)
    fun searchByStartTimeBetween(timeFrom: LocalDateTime?, timeTo: LocalDateTime?): MutableIterable<Event>
}
