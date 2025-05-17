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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.True.Care.helpers.FileUploadHelper;
import com.True.Care.modal.Departments;
import com.True.Care.repository.DepartmentsRepository;
import com.True.Care.repository.DoctorsRespository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping(path = "/")
public class DepartmentsController {

    @Autowired
    private DepartmentsRepository departmentsRepository;
    @Autowired
    private DoctorsRespository doctorsRepository;

    Logger logger = LoggerFactory.getLogger(DepartmentsController.class);
    Map<String, Object> response = new HashMap<>();
    @Value("${app.upload.base-url}")
    private String uploadBaseUrl;

    @PostMapping(path = "/department", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addDepartment(
            @ModelAttribute Departments department,
            @RequestParam(value = "department_picture", required = false) MultipartFile file) {

        logger.info("Set picture file name: {}", file);
        response.clear();

        try {
            // Generate slug from department name
            if (department.getName() != null && !department.getName().isEmpty()) {
                String slug = department.getName().toLowerCase().trim().replaceAll("[^a-z0-9\\s]", "")
                        .replaceAll("\\s+", "-");
                department.setSlug(slug);
            }

            if (file != null && !file.isEmpty()) {
                String uploadedFileUrl = FileUploadHelper.uploadFile(file, uploadBaseUrl);

                if (uploadedFileUrl != null) {
                    logger.info("File uploaded successfully: {}", uploadedFileUrl);
                    department.setDepartmentPicture(uploadedFileUrl);
                }
            }

            departmentsRepository.save(department);
            response.put("success", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/department/{id}")
    public ResponseEntity<Map<String, Object>> getDepartment(@PathVariable("id") Integer id) {
        response.clear();
        try {
            Optional<Departments> departmentOptional = departmentsRepository.findById(id);
            if (departmentOptional.isPresent()) {
                response.put("success", true);
                response.put("data", departmentOptional.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(path = "/department/{id}", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> updateDepartment(@PathVariable("id") Integer id,
            @RequestBody Departments updateDepartmentData) {
        response.clear();
        try {
            Optional<Departments> departmentOptional = departmentsRepository.findById(id);
            if (!departmentOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "invalid id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Departments department = departmentOptional.get();
            department.setName(updateDepartmentData.getName());
            department.setDescription(updateDepartmentData.getDescription());
            departmentsRepository.save(department);

            response.put("success", true);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("statis", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/departments")
    public ResponseEntity<Map<String, Object>> getAllDepartments() {
        response.clear();
        try {
            List<Departments> departmentsData = new ArrayList<>();
            departmentsRepository.findAll().forEach(departmentsData::add);
            response.put("success", true);
            response.put("data", departmentsData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/department/slug")
    public ResponseEntity<Map<String, Object>> getDepartmentBySlug(@RequestParam ("slug") String slug)
    {
        response.clear();
        try {
            Departments department = departmentsRepository.findByslug(slug);
            if (department == null) {
                response.put("success", false);
                response.put("message", "No department found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            response.put("success", true);
            response.put("data", department);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(path = "/department/{id}")
    public ResponseEntity<Map<String, Object>> deleteDepartment(@PathVariable("id") Integer id) {
        response.clear();
        try {
            Optional<Departments> departmentOptional = departmentsRepository.findById(id);
            if (!departmentOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "department not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (isDepartmentReferencedByDoctors(id)) {
                response.put("success", false);
                response.put("message", "Cannot delete department: it is associated with one or more doctors.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            departmentsRepository.deleteById(id);
            response.put("success", true);
            response.put("message", "department deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(path = "/departments")
    public ResponseEntity<Map<String, Object>> deleteAllDepartments() {
        response.clear();
        try {
            List<Departments> allDepartments = (List<Departments>) departmentsRepository.findAll();

            // Find if any department is referenced by a doctor
            for (Departments department : allDepartments) {
                if (doctorsRepository.existsByDepartment_DepartmentId(department.getDepartmentId())) {
                    response.put("success", false);
                    response.put("message", "Cannot delete all departments: One or more departments are associated with doctors.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

            // No references found, safe to delete
            departmentsRepository.deleteAll();
            response.put("success", true);
            response.put("message", "All departments deleted successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Something went wrong.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isDepartmentReferencedByDoctors(Integer departmentId) {
        return doctorsRepository.existsByDepartment_DepartmentId(departmentId);
    }

}
