package com.koroliuk.emms.model.event

import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "events")
class Event(

    var name: String,

    var description: String,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: EventCategory,

    @Column(name = "is_blocked")
    var blocked: Boolean = false,

    @Enumerated(EnumType.STRING)
    var visibilityType: EventVisibilityType,

    @OneToOne(mappedBy = "event", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    @JsonManagedReference
    var offlineEvent: OfflineEvent? = null,

    @OneToOne(mappedBy = "event", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    @JsonManagedReference
    var onlineEvent: OnlineEvent? = null,

    @OneToOne(mappedBy = "event", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    @JsonManagedReference
    var privateEvent: PrivateEvent? = null,

    @OneToOne(mappedBy = "event", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    @JsonManagedReference
    var eventVolunteers: EventVolunteers? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

)
