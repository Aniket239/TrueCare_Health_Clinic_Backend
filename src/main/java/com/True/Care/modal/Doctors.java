package com.True.Care.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalTime;

@Entity
public class Doctors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer doctor_id;

    private String name;
    @ManyToOne
    @JoinColumn(name = "departmentId", nullable = false)
    private Departments department;

    private LocalTime start_time;
    private LocalTime end_time;
    private String about;
    private Integer experience;
    private String profile_image;
    // Getters and setters

    public Integer getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Integer doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public LocalTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalTime start_time) {
        this.start_time = start_time;
    }

    public LocalTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalTime end_time) {
        this.end_time = end_time;
    }

    public String getAbout(){
        return about;
    }

    public void setAbout(String about){
        this.about = about;
    }

    public Integer getExperience(){
        return experience;
    }
    
    public void setExperience(Integer experience){
        this.experience = experience;
    }

    public String getProfile_image(){
        return profile_image;
    }
    
    public void setProfile_image(String profile_image){
        this.profile_image = profile_image;
    }
}
