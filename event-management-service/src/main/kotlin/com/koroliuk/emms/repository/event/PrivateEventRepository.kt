package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.PrivateEvent
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface PrivateEventRepository : CrudRepository<PrivateEvent, Long> {
}