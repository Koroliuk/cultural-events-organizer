package com.koroliuk.emms.repository.group

import com.koroliuk.emms.model.group.GroupManager
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable

@Repository
interface GroupManagerRepository : JpaRepository<GroupManager, Long> {

    fun findAllByGroupId(groupId: Long): List<GroupManager>

    fun findAllByUser(user: User, pageable: Pageable): Page<GroupManager>

    fun deleteByGroupId(id: Long)

}