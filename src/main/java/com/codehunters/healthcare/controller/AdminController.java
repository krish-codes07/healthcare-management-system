package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.model.*;
import com.codehunters.healthcare.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public AdminController(UserRepository userRepository,
                            AppointmentRepository appointmentRepository,
                            DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());

        List<Appointment> allAppointments = appointmentRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Appointment appt : allAppointments) {
            User patient = userRepository.findById(appt.getPatientId()).orElseThrow();
            User doctor = userRepository.findById(appt.getDoctorId()).orElseThrow();
            DoctorAvailability slot = doctorAvailabilityRepository.findById(appt.getSlotId()).orElseThrow();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("patientName", patient.getFullName());
            row.put("doctorName", doctor.getFullName());
            row.put("date", slot.getSlotDate());
            row.put("time", slot.getStartTime());
            row.put("status", appt.getStatus());
            rows.add(row);
        }
        model.addAttribute("appointments", rows);

        return "admin-dashboard";
    }

    @PostMapping("/users/{id}/toggle-active")
    public String toggleActive(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);
        return "redirect:/admin";
    }
}