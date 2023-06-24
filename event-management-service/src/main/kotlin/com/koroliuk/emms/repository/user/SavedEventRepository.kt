package com.koroliuk.emms.repository.user

import com.koroliuk.emms.model.user.SavedEvent
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable


@Repository
interface SavedEventRepository : JpaRepository<SavedEvent, Long> {

    fun findAllByUserOrderByCreatedDateDesc(user: User, pageable: Pageable): Page<SavedEvent>

    fun existsByEventIdAndUser(eventId: Long, user: User): Boolean

}