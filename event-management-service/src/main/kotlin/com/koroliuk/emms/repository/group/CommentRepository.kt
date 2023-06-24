package com.koroliuk.emms.repository.group

import com.koroliuk.emms.model.complaint.ComplaintStatus
import com.koroliuk.emms.model.group.Comment
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable


@Repository
interface CommentRepository : JpaRepository<Comment, Long>{

    fun findByGroupId(groupId: Long): List<Comment>

    @Query(
        """SELECT c FROM Comment c 
        WHERE c.group.id = :groupId AND c.isHidden = :hidden""",
        countQuery = """
            SELECT COUNT(c) FROM Comment c
            WHERE c.group.id = :groupId AND c.isHidden = :hidden"""
    )
    fun findByGroupIdAndHidden(groupId: Long, hidden: Boolean, pageable: Pageable): Page<Comment>


    @Query(
        """
        SELECT COUNT(c)
        FROM Comment c
        JOIN CommentComplaint cc ON cc.comment.id = c.id
        JOIN Complaint comp ON comp.id = cc.id
        WHERE c.author.id = :userId and comp.status = :status
    """
    )
    fun countUserCommentsWithComplaintsByStatus(userId: Long, status: ComplaintStatus): Long

    fun deleteByGroupId(groupId: Long)
}
