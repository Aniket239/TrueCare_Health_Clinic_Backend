package com.True.Care.repository;

import org.springframework.data.repository.CrudRepository;

import com.True.Care.modal.Doctors;

public interface DoctorsRespository extends CrudRepository<Doctors, Integer> {
    boolean existsByDepartment_DepartmentId(Integer departmentId);
} 
