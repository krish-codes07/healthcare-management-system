package com.codehunters.healthcare.repository;

import com.codehunters.healthcare.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
}