package com.koroliuk.repository

import com.koroliuk.model.Event

interface EventRepository {
    fun create(event: Event): Event
    fun findById(id: Long): Event?
    fun findAll(): List<Event>
    fun update(event: Event): Event
    fun deleteById(id: Long): Boolean
}
