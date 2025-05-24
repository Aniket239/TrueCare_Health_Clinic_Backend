package com.True.Care.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;

import com.True.Care.modal.User;
import com.True.Care.repository.UserRepository;
import com.True.Care.util.Encryption;
import com.True.Care.util.JwtUtil;

import io.jsonwebtoken.JwtException;

@Controller // <-- Use this if you want JSON responses by default
@RequestMapping(path = "/") // This means URL's start with /demo (after Application pat)
public class UserController {

    @Autowired
    private Encryption encryption;
    @Autowired // This means to get the bean called userRepository
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    Map<String, Object> response = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(DepartmentsController.class);

    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            String email = user.getEmail();
            String encryptedEmail = encryption.encrypt(email);
            String token = jwtUtil.generateToken(encryptedEmail);
            response.put("success", true);
            response.put("token", token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
            // return ResponseEntity.ok("Saved");
        } catch (DataIntegrityViolationException e) {
            response.put("success", false);
            response.put("token", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/user")
    public ResponseEntity<?> getLoggedInUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 1. Extract token from "Bearer <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
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
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token: " + e.getMessage());
        }
    }

    @PostMapping(path = "/login", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User loginRequest) {
        response.clear();
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            response.put("status", false);
            response.put("message", "user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (user.getPassword().equals(loginRequest.getPassword())) {
            try {
                String email = user.getEmail();
                String encryptedEmail = encryption.encrypt(email);
                String token = jwtUtil.generateToken(encryptedEmail);
                response.put("success", true);
                response.put("token", token);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (Exception e) {
                response.put("success", false);
                response.put("error", "Something went wrong.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping(path = "/user", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader, @RequestBody User userData) {
        response.clear();
        try {
            // 1. Extract token from "Bearer <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7); // Remove "Bearer "

            // 2. Validate token and get encrypted email
            String encryptedEmail = jwtUtil.validateTokenAndRetrieveSubject(token);

            // 3. Decrypt email
            String email = encryption.decrypt(encryptedEmail);

            // 4. Find user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            user.setFirstname(userData.getFirstname());
            user.setLastname(userData.getLastname());
            user.setDOB(userData.getDOB());
            user.setGender(userData.getGender());
            user.setPhoneNumber(userData.getPhoneNumber());
            user.setPassword(userData.getPassword());
            userRepository.save(user);
            response.put("success", true);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
