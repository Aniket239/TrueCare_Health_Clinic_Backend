package com.True.Care.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Departments {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer department_id;
    private String name;
    private String description;
    
    public Integer getDepartmentId(){
        return department_id;
    }

    public void setDepartmentId(Integer department_id){
        this.department_id = department_id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription( String description){
        this.description = description;
    }
}
