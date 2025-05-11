package com.True.Care.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.True.Care.modal.Departments;
import com.True.Care.repository.DepartmentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping(path = "/department")
public class DepartmentsController {

    @Autowired
    private DepartmentsRepository departmentsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(DepartmentsController.class);

    @PostMapping(path = "/add", consumes = "application/json")
    public ResponseEntity<String> addDepartment(@RequestBody Departments department) {
        try {
            logger.info("Received department: {}", objectMapper.writeValueAsString(department));
        } catch (Exception e) {
            logger.warn("Failed to log department JSON", e);
        }
        try {
            departmentsRepository.save(department);
            return ResponseEntity.ok("Saved");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Something went wrong.");
        }
    }

}
