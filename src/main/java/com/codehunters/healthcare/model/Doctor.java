package com.codehunters.healthcare.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "hospital_id")
    private Integer hospitalId;

    private String specialization;
    private String qualification;

    @Column(name = "consult_fee")
    private BigDecimal consultFee;

    // Getters
    public Integer getDoctorId() { return doctorId; }
    public Integer getHospitalId() { return hospitalId; }
    public String getSpecialization() { return specialization; }
    public String getQualification() { return qualification; }
    public BigDecimal getConsultFee() { return consultFee; }
}