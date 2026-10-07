package com.codehunters.healthcare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "appointment_id")
    private Integer appointmentId;

    private String diagnosis;
    private String prescription;
    private String notes;

    public void setPatientId(Integer patientId) { this.patientId = patientId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getRecordId() { return recordId; }
}