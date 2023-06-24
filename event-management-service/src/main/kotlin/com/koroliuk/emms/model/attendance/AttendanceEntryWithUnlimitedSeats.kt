package com.koroliuk.emms.model.attendance

import javax.persistence.*

@Entity
@Table(name = "attendance_entry_with_unlimited_seats")
class AttendanceEntryWithUnlimitedSeats(

    @OneToOne
    @MapsId
    @JoinColumn(name = "attendance_entry_id")
    val attendanceEntry: AttendanceEntry,

    @ManyToOne
    val priceCategory: PriceCategory,

    @Id
    @Column(name = "attendance_entry_id")
    var id: Long? = null
)