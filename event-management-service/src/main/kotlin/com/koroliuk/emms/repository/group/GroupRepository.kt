package com.koroliuk.emms.repository.group

import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool


@Repository
interface GroupRepository : CrudRepository<Group, Long> {

    fun existsByName(name: String): Boolean

}