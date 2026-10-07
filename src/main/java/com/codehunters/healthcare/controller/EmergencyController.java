package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.model.*;
import com.codehunters.healthcare.repository.*;
import com.codehunters.healthcare.util.LocationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Controller
public class EmergencyController {

    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final UserRepository userRepository;

    // No GPS on a server-rendered site — patient picks their area instead.
    private static final Map<String, double[]> AREA_COORDINATES = new LinkedHashMap<>();
    static {
        AREA_COORDINATES.put("Malviya Nagar", new double[]{26.8606, 75.8043});
        AREA_COORDINATES.put("C-Scheme", new double[]{26.9115, 75.7804});
        AREA_COORDINATES.put("Vaishali Nagar", new double[]{26.9123, 75.7373});
        AREA_COORDINATES.put("Mansarovar", new double[]{26.8505, 75.7625});
        AREA_COORDINATES.put("Jagatpura", new double[]{26.8139, 75.8392});
    }

    public EmergencyController(HospitalRepository hospitalRepository,
                                DoctorRepository doctorRepository,
                                DoctorAvailabilityRepository doctorAvailabilityRepository,
                                UserRepository userRepository) {
        this.hospitalRepository = hospitalRepository;
        this.doctorRepository = doctorRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/emergency")
    public String emergencyForm(Model model) {
        model.addAttribute("areas", AREA_COORDINATES.keySet());
        return "emergency";
    }

    @GetMapping("/emergency/results")
    public String emergencyResults(@RequestParam String area, Model model) {
        double[] patientCoords = AREA_COORDINATES.get(area);
        List<Hospital> hospitals = hospitalRepository.findAll();

        List<Map<String, Object>> results = new ArrayList<>();
        for (Hospital h : hospitals) {
            double distance = LocationUtil.distanceKm(
                patientCoords[0], patientCoords[1], h.getLatitude(), h.getLongitude());

            List<Doctor> hospitalDoctors = doctorRepository.findByHospitalId(h.getHospitalId());
            List<String> availableDoctors = new ArrayList<>();

            for (Doctor d : hospitalDoctors) {
                List<DoctorAvailability> freeSlots = doctorAvailabilityRepository
                    .findByDoctorIdAndSlotDateAndIsBooked(d.getDoctorId(), LocalDate.now(), false);
                if (!freeSlots.isEmpty()) {
                    User doctorUser = userRepository.findById(d.getDoctorId()).orElseThrow();
                    availableDoctors.add(doctorUser.getFullName() + " (" + d.getSpecialization() + ")");
                }
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", h.getName());
            row.put("address", h.getAddress());
            row.put("distance", Math.round(distance * 10) / 10.0);
            row.put("availableDoctors", availableDoctors);
            results.add(row);
        }

        results.sort(Comparator.comparingDouble(r -> (Double) r.get("distance")));

        model.addAttribute("area", area);
        model.addAttribute("results", results);
        return "emergency-results";
    }
}