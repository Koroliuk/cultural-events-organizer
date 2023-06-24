package com.koroliuk.emms.model.complaint

import com.koroliuk.emms.model.user.User
import javax.persistence.*

@Entity
@Table(name = "complaints")
class Complaint(

    @ManyToOne
    val author: User,

    val reason: String? = null,

    @Enumerated(EnumType.STRING)
    var status: ComplaintStatus,

    @OneToOne(mappedBy = "complaint", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var commentComplaint: CommentComplaint? = null,

    @OneToOne(mappedBy = "complaint", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var eventComplaint: EventComplaint? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

)
