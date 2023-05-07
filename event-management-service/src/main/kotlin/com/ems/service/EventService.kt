package com.ems.service

import com.ems.model.Event
import java.time.LocalDateTime

interface EventService {

    fun create(event: Event): Event

    fun update(event: Event): Event

    fun findById(id: Long): Event

    fun findAll(): MutableIterable<Event>

    fun searchEvents(keywords: List<String>?, categories: List<String>?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): MutableIterable<Event>

    fun existById(id: Long): Boolean

    fun deleteById(id: Long)

}
