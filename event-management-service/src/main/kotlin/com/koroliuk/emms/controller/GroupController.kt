package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.dto.*
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE_SIZE
import com.koroliuk.emms.utils.ControllerUtils.getCurrentUser
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.GroupService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal
import javax.validation.Valid

@Controller("/api/groups")
class GroupController(
    @Inject private val userService: UserService,
    @Inject private val groupService: GroupService
) {

    @Get("/{groupId}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getGroupById(@PathVariable groupId: Long): HttpResponse<GroupInfo> {
        val group = groupService.getGroupInfoById(groupId) ?: return HttpResponse.notFound()
        return HttpResponse.ok(group)
    }

    @Post
    @Secured(USER)
    fun createGroup(@Valid @Body groupDto: GroupDto, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (groupService.existsByName(groupDto.name)) {
            return HttpResponse.status<Any?>(HttpStatus.CONFLICT)
                .body(createMessageResponse("A group with this name already exists"))
        }
        val createdGroup = groupService.createGroup(groupDto.name, user)
        return HttpResponse.created(createdGroup)
    }

    @Put("/{groupId}")
    @Secured(USER)
    fun updateGroup(
        @PathVariable groupId: Long,
        @Valid @Body groupDto: GroupDto,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val group = groupService.findById(groupId) ?: return HttpResponse.notFound()
        if (!groupService.isOwner(user, group)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not a group owner"))
        }
        if (group.isBlocked) {
            return HttpResponse.status<GroupInfo?>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("This group is blocked"))
        }
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val updatedGroup = groupService.updateGroup(group, groupDto)
        return HttpResponse.ok(updatedGroup)
    }

    @Delete("/{groupId}")
    @Secured(USER)
    fun deleteGroup(@PathVariable groupId: Long, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        if (!groupService.isOwner(user, group)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not a group owner"))
        }
        groupService.deleteGroup(groupId)
        return HttpResponse.noContent()
    }

    @Get("/{groupId}/events")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getGroupEvents(
        @PathVariable groupId: Long,
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): HttpResponse<Any> {
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        val events = groupService.findGroupEvents(group, page, size)
        return HttpResponse.ok(events)
    }

    @Get("/{groupId}/subscribers")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getGroupSubscribers(
        @PathVariable groupId: Long,
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): HttpResponse<Any> {
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        val subscribers = groupService.findGroupSubscribers(group, page, size)
        return HttpResponse.ok(subscribers)
    }

    @Post("/{groupId}/comments")
    @Secured(USER)
    fun createCommentInGroup(
        @PathVariable groupId: Long,
        @Valid @Body commentDto: CommentCreateDto,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        if (group.isBlocked) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("This group is blocked"))
        }
        val createdComment = groupService.createComment(group, commentDto, user)
        return HttpResponse.created(createdComment)
    }

    @Delete("/{groupId}/comments/{commentId}")
    @Secured(USER)
    fun deleteComment(
        @PathVariable groupId: Long,
        @PathVariable commentId: Long,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val comment = groupService.findCommentById(commentId) ?: return HttpResponse.badRequest()
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        if (comment.group.id != group.id) {
            return HttpResponse.badRequest()
        }
        if (!groupService.isUserAuthorOfComment(comment, user)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        groupService.hideComment(comment)
        return HttpResponse.noContent()
    }

    @Get("/{groupId}/comments")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getGroupComments(
        @PathVariable groupId: Long,
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): HttpResponse<Any> {
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        return HttpResponse.ok(groupService.findCommentsOfGroup(group, page, size))
    }

    @Post("/{groupId}/managers")
    @Secured(USER)
    fun addManagerToGroup(
        @PathVariable groupId: Long,
        @Valid @Body managerDto: ManagerDto,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        if (!groupService.isOwner(user, group)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not a group owner"))
        }
        if (group.isBlocked) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("This group is blocked"))
        }
        val manager = userService.findByUsername(managerDto.username) ?: return HttpResponse.badRequest()
        if (manager.isBlocked) {
            return HttpResponse.badRequest<Any?>()
                .body(createMessageResponse("User to add as manager is blocked"))
        }
        val updatedGroup = groupService.addManager(group, manager)
        return HttpResponse.ok(updatedGroup)
    }

    @Delete("/{groupId}/managers/{managerId}")
    @Secured(USER)
    fun deleteManagerToGroup(
        @PathVariable groupId: Long,
        @PathVariable managerId: Long,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        if (!groupService.isOwner(user, group)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not a group owner"))
        }
        if (group.managers.stream().noneMatch { it.id == managerId }) {
            return HttpResponse.badRequest()
        }
        val updatedGroup = groupService.deleteManager(group, managerId)
        return HttpResponse.ok(updatedGroup)
    }

    @Post("/{groupId}/announce")
    @Secured(USER)
    fun announce(@PathVariable groupId: Long, @Body announceDto: AnnounceDto, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val group = groupService.findById(groupId) ?: return HttpResponse.badRequest()
        if (!groupService.isOwner(user, group)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        groupService.announce(groupId, announceDto)
        return HttpResponse.ok()
    }

}
