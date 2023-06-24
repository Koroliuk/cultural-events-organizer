package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.*
import com.koroliuk.emms.model.group.GroupSubscription
import com.koroliuk.emms.model.user.SavedEvent
import com.koroliuk.emms.model.user.SavedGroup
import com.koroliuk.emms.model.user.User


interface UserService {

    fun create(user: User)

    fun update(user: User)

    fun existByUsername(username: String): Boolean

    fun findByUsername(username: String): User?

    fun saveEvent(eventId: Long, user: User): SavedEvent

    fun findSavedEventById(id: Long): SavedEvent?

    fun getAllSavedEventsByUser(page: Int, size: Int, user: User): GetSavedEventsResponse

    fun isEventSavedByUser(eventId: Long, user: User): Boolean


    fun deleteSavedEventById(id: Long)

    fun findSavedGroupById(id: Long): SavedGroup?

    fun isGroupSavedByUser(groupId: Long, user: User): Boolean

    fun getAllSavedGroupsByUser(page: Int, size: Int, user: User): GetSavedGroupsResponse

    fun deleteSavedGroupById(id: Long)

    fun isSubcribetToThisGroup(groupId: Long, user: User): Boolean

    fun deleteSubscriptionByGroupIdAndUser(groupId: Long, user: User)

    fun complianAboutEvent(eventId: Long, user: User)

    fun complianAboutComment(eventId: Long, user: User)

    fun getUserComplaints(page: Int, size: Int, user: User): GetComplaintsResponse

    fun getUserVolunteerApplications(user: User, page: Int, size: Int): GetVolunteerApplicationsResponse

    fun getUserSubscriptions(page: Int, size: Int, user: User): GetGroupSubscriptionsResponse
}
