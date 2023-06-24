package com.koroliuk.emms.repository.notification

import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.notification.Notification
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime


@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {

    fun findByUserAndCreatedAfter(user: User, since: LocalDateTime): List<Notification>

    fun findByUser(user: User): List<Notification>

    //todo: create scheduled job to take new updates
    @Query("SELECT n FROM Notification n WHERE n.created > :timestamp")
    fun findNewNotifications(timestamp: LocalDateTime?): List<Notification>

    fun deleteByGroupId(groupId: Long)

}
