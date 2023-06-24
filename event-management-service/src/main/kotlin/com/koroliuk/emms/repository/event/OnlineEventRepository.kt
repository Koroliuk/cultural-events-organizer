package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.OnlineEvent
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface OnlineEventRepository : CrudRepository<OnlineEvent, Long> {

    fun findByEventId(id: Long): OnlineEvent?


}
