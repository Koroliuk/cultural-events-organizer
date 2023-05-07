package com.ems.service

import com.ems.model.Event

interface EventService {

    fun create(event: Event): Event

    fun update(event: Event): Event

    fun findById(id: Long): Event

    fun findAll(): MutableIterable<Event>

    fun searchEventsByKeywords(keywords: List<String>): MutableIterable<Event>

    fun existById(id: Long): Boolean

    fun deleteById(id: Long)

}
