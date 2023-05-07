package com.koroliuk.ncs

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime

@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {

    fun deleteByCreatedBefore(created: LocalDateTime)

}
