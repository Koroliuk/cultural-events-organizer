package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.OfflineEvent
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface OfflineEventRepository : CrudRepository<OfflineEvent, Long> {

    fun findByEventId(id: Long): OfflineEvent?

}
