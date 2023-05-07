package com.koroliuk.ems

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {
}
