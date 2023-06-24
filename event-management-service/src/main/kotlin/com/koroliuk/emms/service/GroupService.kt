package com.koroliuk.emms.service

import com.koroliuk.emms.controller.dto.*
import com.koroliuk.emms.controller.response.GetCommentsResponse
import com.koroliuk.emms.controller.response.GetEventInfoResponse
import com.koroliuk.emms.controller.response.GetGroupResponse
import com.koroliuk.emms.controller.response.GetGroupSubscriptionsResponse
import com.koroliuk.emms.model.group.Comment
import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.group.GroupSubscription
import com.koroliuk.emms.model.user.SavedGroup
import com.koroliuk.emms.model.user.User

interface GroupService {

    fun createGroup(name: String, user: User): GroupInfo

    fun updateGroup(group: Group, groupDto: GroupDto): GroupInfo

    fun deleteGroup(id: Long)

    fun createComment(group: Group, commentDto: CommentCreateDto, user: User): CommentDto

    fun isOwner(user: User, group: Group): Boolean

    fun getGroupManagers(group: Group): List<User>

    fun existsById(id: Long): Boolean

    fun existsByName(name: String): Boolean

    fun commentExistsById(id: Long): Boolean

    fun findById(id: Long): Group?

    fun getGroupInfoById(id: Long): GroupInfo?

    fun isUserAuthorOfComment(comment: Comment, user: User): Boolean

    fun hideComment(comment: Comment)

    fun findCommentById(id: Long): Comment?

    fun findCommentsOfGroup(group: Group, page: Int, size: Int): GetCommentsResponse

    fun findGroupEvents(group: Group, page: Int, size: Int): GetEventInfoResponse

    fun findGroupSubscribers(group: Group, page: Int, size: Int): GetGroupSubscriptionsResponse

    fun addManager(group: Group, manager: User): GroupInfo

    fun deleteManager(group: Group, managerId: Long): GroupInfo

    fun saveGroup(groupId: Long, user: User): SavedGroup

    fun subcribeToGroup(groupId: Long, user: User): GroupSubscription

    fun announce(groudId: Long, announceDto: AnnounceDto)

    fun getGroupByUser(user: User, page: Int, size: Int): GetGroupResponse

}