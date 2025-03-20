package com.reliaquest.api.model;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class EmployeeRequest {

    private String name;

    private Integer salary;

    private Integer age;

    private String title;

    private String email;

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
