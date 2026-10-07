package com.codehunters.healthcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "slot_id")
    private Integer slotId;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String reason;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    public Integer getAppointmentId() { return appointmentId; }
    public Integer getPatientId() { return patientId; }
    public Integer getDoctorId() { return doctorId; }
    public Integer getSlotId() { return slotId; }
    public AppointmentStatus getStatus() { return status; }
    public String getReason() { return reason; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }
}