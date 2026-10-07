package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.model.*;
import com.codehunters.healthcare.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public DoctorController(UserRepository userRepository,
                             AppointmentRepository appointmentRepository,
                             DoctorAvailabilityRepository doctorAvailabilityRepository,
                             MedicalRecordRepository medicalRecordRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User doctorUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorUser.getUserId());

        List<Map<String, Object>> rows = new ArrayList<>();
        for (Appointment appt : appointments) {
            User patientUser = userRepository.findById(appt.getPatientId()).orElseThrow();
            DoctorAvailability slot = doctorAvailabilityRepository.findById(appt.getSlotId()).orElseThrow();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("appointmentId", appt.getAppointmentId());
            row.put("patientName", patientUser.getFullName());
            row.put("date", slot.getSlotDate());
            row.put("time", slot.getStartTime());
            row.put("status", appt.getStatus());
            row.put("reason", appt.getReason());
            rows.add(row);
        }

        model.addAttribute("doctorName", doctorUser.getFullName());
        model.addAttribute("appointments", rows);
        return "doctor-dashboard";
    }

    @PostMapping("/appointments/{id}/confirm")
    public String confirm(@PathVariable Integer id) {
        Appointment appt = appointmentRepository.findById(id).orElseThrow();
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);
        return "redirect:/doctor";
    }

    @PostMapping("/appointments/{id}/reject")
    public String reject(@PathVariable Integer id) {
        Appointment appt = appointmentRepository.findById(id).orElseThrow();
        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
        return "redirect:/doctor";
    }

    @GetMapping("/appointments/{id}/record")
    public String recordForm(@PathVariable Integer id, Model model) {
        Appointment appt = appointmentRepository.findById(id).orElseThrow();
        User patientUser = userRepository.findById(appt.getPatientId()).orElseThrow();
        model.addAttribute("appointmentId", id);
        model.addAttribute("patientName", patientUser.getFullName());
        return "add-record";
    }

    @PostMapping("/appointments/{id}/record")
    public String saveRecord(@PathVariable Integer id,
                              @RequestParam String diagnosis,
                              @RequestParam String prescription,
                              @RequestParam(required = false) String notes) {
        Appointment appt = appointmentRepository.findById(id).orElseThrow();

        MedicalRecord record = new MedicalRecord();
        record.setPatientId(appt.getPatientId());
        record.setDoctorId(appt.getDoctorId());
        record.setAppointmentId(id);
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setNotes(notes);
        medicalRecordRepository.save(record);

        appt.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appt);

        return "redirect:/doctor";
    }
}