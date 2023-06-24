package com.koroliuk.ncs.repository

import com.koroliuk.ncs.model.Notification
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime


@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {

    @Query("""
    DELETE FROM notifications n 
    WHERE n.id IN (
        SELECT id FROM notifications
        WHERE created < :created
        ORDER BY id LIMIT :batchSize)""",
        nativeQuery = true
    )
    fun deleteByCreatedBefore(created: LocalDateTime, batchSize: Int)

}
