package com.koroliuk.emms.repository.complaint

import com.koroliuk.emms.controller.dto.ComplaintInfo
import com.koroliuk.emms.model.complaint.Complaint
import com.koroliuk.emms.model.complaint.ComplaintStatus
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable

@Repository
interface ComplaintRepository : JpaRepository<Complaint, Long> {

    @Query(
        """
        UPDATE Complaint c
        SET c.status = :status
        WHERE c.id = :id
    """
    )
    fun updateStatusById(id: Long, status: ComplaintStatus)

    @Query(
            """SELECT c FROM Complaint c
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        WHERE ec.event.id = :eventId AND (c.status = :status OR :status is null)""",
            countQuery = """SELECT count(c) FROM Complaint c
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        WHERE ec.event.id = :eventId AND (c.status = :status OR :status is null)"""
    )
    fun findByEventIdAndStatus(eventId: Long, status: ComplaintStatus?, pageable: Pageable): Page<Complaint>


    @Query(
        """
        SELECT
            u.username AS authorUsername,
            c.reason AS reason,
            c.status AS status,
            e.id AS eventId,
            cm.id AS commentId,
            c.id AS id
        FROM Complaint c
        JOIN c.author u
        LEFT JOIN CommentComplaint cc ON cc.id = c.id
        LEFT JOIN Comment cm ON cc.comment.id =  cm.id
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        LEFT JOIN Event e ON ec.event.id = e.id
        WHERE (:status IS NULL OR :status = c.status)
        """,
        countQuery = """
        SELECT COUNT(*)
         FROM Complaint c
        JOIN c.author u
        LEFT JOIN CommentComplaint cc ON cc.id = c.id
        LEFT JOIN Comment cm ON cc.comment.id =  cm.id
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        LEFT JOIN Event e ON ec.event.id = e.id
        WHERE (:status IS NULL OR :status = c.status)
        """
    )
    fun findAllComplaintsByStatus1(status: ComplaintStatus?, pageable: Pageable): Page<ComplaintInfo>


    fun findByStatus(status: ComplaintStatus?, pageable: Pageable): Page<Complaint>


    @Query(
        """
        SELECT 
            c.id AS id,
            u.username AS authorUsername, 
            c.reason AS reason,
            c.status AS status,
            e.id AS eventId,
            cm.id AS commentId
        FROM Complaint c
        JOIN c.author u
        LEFT JOIN CommentComplaint cc ON cc.id = c.id
        LEFT JOIN cc.comment cm
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        LEFT JOIN ec.event e
        WHERE c.id = :id
    """
    )
    fun getComplaintInfoById(id: Long): ComplaintInfo?

    @Query(
        """
        SELECT
            u.username AS authorUsername, 
            c.reason AS reason,
            c.status AS status,
            e.id AS eventId,
            cm.id AS commentId,
            c.id AS id
        FROM Complaint c
        JOIN c.author u
        LEFT JOIN CommentComplaint cc ON cc.id = c.id
        LEFT JOIN cc.comment cm
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        LEFT JOIN ec.event e
        WHERE (:username IS NULL OR :username = u.username)
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM Complaint c
        JOIN c.author u
        LEFT JOIN CommentComplaint cc ON cc.id = c.id
        LEFT JOIN cc.comment cm
        LEFT JOIN EventComplaint ec ON ec.id = c.id
        LEFT JOIN ec.event e
        WHERE (:username IS NULL OR :username = u.username)
        """
    )
    fun findInfoByAuthor(username: String, pageable: Pageable): Page<ComplaintInfo>


    @Query(
        """SELECT count(c) FROM Complaint c  
        JOIN EventComplaint ec ON ec.id = c.id
           WHERE c.id = :groupId AND c.status = :status"""
    )
    fun countGroupComplaints(groupId: Long, status: ComplaintStatus = ComplaintStatus.APPROVED): Long

}
