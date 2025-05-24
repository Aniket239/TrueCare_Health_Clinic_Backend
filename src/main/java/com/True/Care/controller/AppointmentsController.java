package com.True.Care.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.True.Care.modal.Appointments;
import com.True.Care.modal.Departments;
import com.True.Care.modal.Doctors;
import com.True.Care.modal.User;
import com.True.Care.repository.AppointmentsRepository;
import com.True.Care.repository.DepartmentsRepository;
import com.True.Care.repository.DoctorsRespository;
import com.True.Care.repository.UserRepository;
import com.True.Care.security.GenerateRandomId;
import com.True.Care.util.Encryption;
import com.True.Care.util.JwtUtil;

import io.jsonwebtoken.JwtException;

@Controller
@RequestMapping(path = "/")
public class AppointmentsController {

    @Autowired
    private AppointmentsRepository appointmentsRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentsRepository departmentsRepository;

    @Autowired
    private DoctorsRespository doctorsRespository;

    @Autowired
    private Encryption encryption;

    @Autowired
    private JwtUtil jwtUtil;

    Map<String, Object> response = new HashMap<>();

    @PostMapping(path = "/appointments", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> addAppointment(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Appointments appointmentData) {
        response.clear();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = authHeader.substring(7); // Remove "Bearer "

            // 2. Validate token and get encrypted email
            String encryptedEmail;
            try {
                encryptedEmail = jwtUtil.validateTokenAndRetrieveSubject(token);
            } catch (JwtException e) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // 3. Decrypt email
            String email = encryption.decrypt(encryptedEmail);

            // 4. Find user by email
            User user = userRepository.findByEmail(email);
            if (appointmentData != null) {
                Integer departmentId = appointmentData.getDepartment().getDepartmentId();
                Integer doctorId = appointmentData.getDoctor().getDoctor_id();

                Departments department = departmentsRepository.findById(departmentId)
                        .orElseThrow(() -> new RuntimeException("Invalid department ID"));
                Doctors doctor = doctorsRespository.findById(doctorId)
                        .orElseThrow(() -> new RuntimeException("Invalid doctor ID"));

                appointmentData.setDepartment(department);
                appointmentData.setDoctor(doctor);
                appointmentData.setUser(user);
                String appointmentId = GenerateRandomId.generateAppointmentId(8);
                appointmentData.setAppointmentId(appointmentId);
                Appointments appointment = appointmentsRepository.save(appointmentData);
                response.put("message", "Appointment added successfully");
                response.put("appointment", appointment);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("error", "Invalid appointment data");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/appointments")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        response.clear();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = authHeader.substring(7); // Remove "Bearer "

            // 2. Validate token and get encrypted email
            String encryptedEmail;
            try {
                encryptedEmail = jwtUtil.validateTokenAndRetrieveSubject(token);
            } catch (JwtException e) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // 3. Decrypt email
            String email = encryption.decrypt(encryptedEmail);

            // 4. Find user by email
            User user = userRepository.findByEmail(email);
            List<Appointments> appointmentsData = new ArrayList<>();
            appointmentsRepository.findAllByUserId(user.getId()).forEach(appointmentsData::add);
            if (appointmentsData.isEmpty()) {
                response.put("success", true);
                response.put("message", "No data found");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            response.put("success", true);
            response.put("data", appointmentsData);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
