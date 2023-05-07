package com.ems.repository

import com.ems.model.Event
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface EventRepository : CrudRepository<Event, Long> {

    @Query("""
        SELECT * FROM events
        WHERE (:keywords) IS NULL
        OR LOWER(name) SIMILAR TO LOWER(:keywords)
        OR LOWER(description) SIMILAR TO LOWER(:keywords)
        OR LOWER(location) SIMILAR TO LOWER(:keywords)
    """, nativeQuery = true)
    fun searchEventsByKeywords(keywords: String?): MutableIterable<Event>

}
