package com.codehunters.healthcare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Integer hospitalId;

    private String name;
    private String address;
    // private String city;
    private Double latitude;
    private Double longitude;
    // private String phone;

    @Column(name = "emergency_available")
    private Boolean emergencyAvailable;

    public Integer getHospitalId() { return hospitalId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}