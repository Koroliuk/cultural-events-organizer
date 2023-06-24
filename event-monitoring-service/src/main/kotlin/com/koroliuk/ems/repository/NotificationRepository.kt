package com.koroliuk.ems.repository

import com.koroliuk.ems.model.Notification
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {
}
