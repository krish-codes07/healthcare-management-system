package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.DoctorAvailability;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {
    List<DoctorAvailability> findByDoctorIdAndSlotDateAndIsBooked(Integer doctorId, LocalDate slotDate, Boolean isBooked);
}