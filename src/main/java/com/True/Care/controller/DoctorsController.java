package com.True.Care.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.True.Care.helpers.FileUploadHelper;
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
    Logger logger = LoggerFactory.getLogger(DoctorsController.class);

    @Value("${app.upload.base-url}")
    private String uploadBaseUrl;

    @PostMapping(path = "/doctor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addDoctor(@ModelAttribute Doctors doctor, @RequestParam(value = "profileImage", required = false) MultipartFile file) {
        response.clear();
        try {
            Integer deptId = doctor.getDepartment().getDepartmentId();
            Optional<Departments> department = departmentsRepository.findById(deptId);
            if (department.isPresent()) {
                if (file != null && !file.isEmpty()) {
                    String uploadedFileUrl = FileUploadHelper.uploadFile(file, uploadBaseUrl);
                    logger.info("File uploaded successfully: {}", uploadedFileUrl, file, uploadBaseUrl);

                    if (uploadedFileUrl != null) {
                        doctor.setProfile_image(uploadedFileUrl);
                    }
                }
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

    @GetMapping(path = "/doctor/{id}")
    public ResponseEntity<Map<String, Object>> getDoctor(@PathVariable("id") Integer id) {
        response.clear();
        try {
            Optional<Doctors> doctor = doctorsRespository.findById(id);
            if (!doctor.isPresent()) {
                response.put("success", false);
                response.put("message", "doctor not found");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                response.put("success", true);
                response.put("message", "doctor found successfully");
                response.put("data", doctor);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/doctors")
    public ResponseEntity<Map<String, Object>> getDoctors() {
        response.clear();
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

    @GetMapping(path = "/doctors/departmentId")
    public ResponseEntity<Map<String, Object>> getDoctorsByDepartment(
            @RequestParam("departmentId") Integer departmentId) {
        response.clear();
        try {
            List<Doctors> doctors = doctorsRespository.findAllByDepartment_DepartmentId(departmentId);
            logger.info("doctors are ================================== {}", doctors);
    
            if (doctors == null || doctors.isEmpty()) {
                response.put("success", false);
                response.put("message", "No doctor found in this department");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
    
            response.put("success", true);
            response.put("data", doctors);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            response.put("exception", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(path = "doctor/{id}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable("id") Integer id) {
        response.clear();
        try {
            Optional<Doctors> doctorOptional = doctorsRespository.findById(id);
            if (!doctorOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "no doctor found with this id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            response.put("success", true);
            response.put("message", "doctor deleted successfully");
            doctorsRespository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(path = "doctors")
    public ResponseEntity<Map<String, Object>> deleteAllDoctors() {
        response.clear();
        try {
            doctorsRespository.deleteAll();
            response.put("seccess", true);
            response.put("message", "all doctors deleted");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
