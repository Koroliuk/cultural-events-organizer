package com.koroliuk.emms.service

import com.koroliuk.emms.controller.dto.ComplaintInfo
import com.koroliuk.emms.controller.response.GetComplaintsResponse
import com.koroliuk.emms.model.complaint.Complaint
import com.koroliuk.emms.model.complaint.ComplaintStatus
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.group.Comment
import com.koroliuk.emms.model.user.User

interface ComplaintService {

    fun complainAboutEvent(event: Event, reason: String, user: User): ComplaintInfo

    fun complainAboutComment(comment: Comment, reason: String, user: User): ComplaintInfo

    fun getEventComplaints(id: Long, page: Int, size: Int): GetComplaintsResponse

    fun findComplaintInfoById(id: Long): ComplaintInfo?

    fun findComplaintById(id: Long): Complaint?

    fun delete(complaint: Complaint)

    fun reject(complaint: Complaint)

    fun approve(complaint: Complaint)

    fun getAllComplaintInfos(pageNumber: Int, size: Int, status: ComplaintStatus?): GetComplaintsResponse

}
