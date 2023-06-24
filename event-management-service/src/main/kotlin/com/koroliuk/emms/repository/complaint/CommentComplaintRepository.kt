package com.koroliuk.emms.repository.complaint

import com.koroliuk.emms.model.complaint.CommentComplaint
import com.koroliuk.emms.model.complaint.EventComplaint
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface CommentComplaintRepository : CrudRepository<CommentComplaint, Long> {

    fun existsByComplaintId(complaintId: Long): Boolean

    fun findByComplaintId(complaintId: Long): CommentComplaint?

    fun findAllByCommentId(commentId: Long): List<CommentComplaint>

}