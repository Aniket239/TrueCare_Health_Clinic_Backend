package com.True.Care.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.True.Care.modal.Departments;
import com.True.Care.modal.Doctors;
import com.True.Care.repository.DepartmentsRepository;
import com.True.Care.repository.DoctorsRespository;

@Controller
@RequestMapping(path = "/")
public class DoctorsController {

    @Autowired
    private DoctorsRespository doctorsRespository;

    @Autowired
    private DepartmentsRepository departmentsRepository;
    Map<String, Object> response = new HashMap<>();

    @PostMapping(path = "/doctor", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> addDoctor(@RequestBody Doctors doctor) {
        try {
            Integer deptId = doctor.getDepartment().getDepartmentId();
            Optional<Departments> department = departmentsRepository.findById(deptId);
            if (department.isPresent()) {
                doctorsRespository.save(doctor);
                response.put("success", true);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("status", false);
                response.put("message", "invalid department id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/doctors/{id}")
    public ResponseEntity<Map<String, Object>> getDoctor(@PathVariable ("id") Integer id){
        try {
            Optional<Doctors> doctor = doctorsRespository.findById(id);
            if(!doctor.isPresent()){
                response.put("success", false);
                response.put("message", "doctor not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            else{
                response.put("success", true);
                response.put("message", "doctor found successfully");
                response.put("data", doctor);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);        }
    }

    @GetMapping(path = "/doctors")
    public ResponseEntity<Map<String, Object>> getDoctors() {
        try {
            List<Doctors> doctorsArray = new ArrayList<>();
            doctorsRespository.findAll().forEach(doctorsArray::add);
            response.put("success", true);
            response.put("data", doctorsArray);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
