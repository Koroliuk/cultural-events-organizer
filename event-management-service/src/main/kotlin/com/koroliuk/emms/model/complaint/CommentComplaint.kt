package com.koroliuk.emms.model.complaint

import com.koroliuk.emms.model.group.Comment
import javax.persistence.*


@Entity
@Table(name = "comment_complaints")
class CommentComplaint(

    @OneToOne
    @MapsId
    @JoinColumn(name = "complaint_id")
    val complaint: Complaint,

    @ManyToOne
    val comment: Comment,

    @Id
    @Column(name = "complaint_id")
    var id: Long? = null

)