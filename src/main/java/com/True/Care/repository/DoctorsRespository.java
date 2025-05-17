package com.True.Care.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.True.Care.modal.Doctors;

public interface DoctorsRespository extends CrudRepository<Doctors, Integer> {
    boolean existsByDepartment_DepartmentId(Integer departmentId);
    List<Doctors> findAllByDepartment_DepartmentId(Integer departmentId);
} 
