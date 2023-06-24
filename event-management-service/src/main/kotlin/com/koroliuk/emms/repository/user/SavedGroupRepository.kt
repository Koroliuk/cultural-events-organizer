package com.koroliuk.emms.repository.user

import com.koroliuk.emms.model.user.SavedGroup
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable


@Repository
interface SavedGroupRepository : JpaRepository<SavedGroup, Long> {

    fun findAllByUserOrderByCreatedDateDesc(user: User, pageable: Pageable): Page<SavedGroup>

    fun existsByGroupIdAndUser(groupId: Long, user: User): Boolean

    fun deleteByGroupId(groupId: Long)
}
