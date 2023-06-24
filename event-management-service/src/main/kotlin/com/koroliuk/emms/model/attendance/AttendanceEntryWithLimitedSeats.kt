package com.koroliuk.emms.model.attendance

import javax.persistence.*


@Entity
@Table(name = "attendance_entry_with_limited_seats")
class AttendanceEntryWithLimitedSeats(

    @OneToOne
    @MapsId
    @JoinColumn(name = "attendance_entry_id")
    val attendanceEntry: AttendanceEntry,

    @ManyToOne
    val seat: Seat,

    @Id
    @Column(name = "attendance_entry_id")
    var id: Long? = null

)
