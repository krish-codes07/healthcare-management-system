package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.model.*;
import com.codehunters.healthcare.repository.*;
import com.codehunters.healthcare.service.AppointmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/patient/book")
public class BookingController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentService appointmentService;

    public BookingController(DoctorRepository doctorRepository,
                              UserRepository userRepository,
                              DoctorAvailabilityRepository doctorAvailabilityRepository,
                              AppointmentService appointmentService) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listDoctors(Model model) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Doctor d : doctors) {
            User u = userRepository.findById(d.getDoctorId()).orElseThrow();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("doctorId", d.getDoctorId());
            row.put("name", u.getFullName());
            row.put("specialization", d.getSpecialization());
            row.put("fee", d.getConsultFee());
            rows.add(row);
        }
        model.addAttribute("doctors", rows);
        return "book-doctor-list";
    }

    @GetMapping("/{doctorId}")
    public String showSlots(@PathVariable Integer doctorId, Model model) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        User doctorUser = userRepository.findById(doctorId).orElseThrow();

        List<DoctorAvailability> todaySlots = doctorAvailabilityRepository
            .findByDoctorIdAndSlotDateAndIsBooked(doctorId, LocalDate.now(), false);
        List<DoctorAvailability> tomorrowSlots = doctorAvailabilityRepository
            .findByDoctorIdAndSlotDateAndIsBooked(doctorId, LocalDate.now().plusDays(1), false);

        model.addAttribute("doctorId", doctorId);
        model.addAttribute("doctorName", doctorUser.getFullName());
        model.addAttribute("specialization", doctor.getSpecialization());
        model.addAttribute("todaySlots", todaySlots);
        model.addAttribute("tomorrowSlots", tomorrowSlots);
        return "book-slots";
    }

    @PostMapping
    public String bookSlot(@RequestParam Integer doctorId,
                            @RequestParam Integer slotId,
                            @RequestParam(required = false) String reason,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User patientUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        try {
            appointmentService.bookAppointment(patientUser.getUserId(), doctorId, slotId, reason);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return showSlots(doctorId, model);
        }

        return "redirect:/patient";
    }
}