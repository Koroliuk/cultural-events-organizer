package com.koroliuk.emms.model.attendance

import com.koroliuk.emms.model.user.User
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "attendance_entries")
class AttendanceEntry(

    @ManyToOne
    val user: User,

    @Column(name = "purchase_time")
    val purchaseTime: LocalDateTime,

    @Enumerated(EnumType.STRING)
    var status: AttendanceEntryStatus,

    var priceToPay: BigDecimal,

    @OneToOne(mappedBy = "attendanceEntry", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var attendanceEntryWithLimitedSeats: AttendanceEntryWithLimitedSeats? = null,

    @OneToOne(mappedBy = "attendanceEntry", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    val attendanceEntryWithUnlimitedSeats: AttendanceEntryWithUnlimitedSeats? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

)
