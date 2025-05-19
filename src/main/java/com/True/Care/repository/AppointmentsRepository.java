package com.True.Care.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.True.Care.modal.Appointments;

public interface AppointmentsRepository extends CrudRepository<Appointments, Integer> {
    List<Appointments> findAllByUserId(Integer Id); 
}
