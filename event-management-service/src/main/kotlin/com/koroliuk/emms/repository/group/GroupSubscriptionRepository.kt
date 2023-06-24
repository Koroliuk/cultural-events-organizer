package com.koroliuk.emms.repository.group

import com.koroliuk.emms.model.group.GroupSubscription
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable


@Repository
interface GroupSubscriptionRepository : JpaRepository<GroupSubscription, Long> {

    fun existsByUserAndGroupId(user: User, groupId: Long): Boolean

    fun findAllByUser(user: User, pageable: Pageable): Page<GroupSubscription>

    fun findAllByGroupId(groupId: Long, pageable: Pageable): Page<GroupSubscription>

    fun deleteByGroupId(id: Long)

    fun deleteByGroupIdAndUser(groupId: Long, user: User)

}