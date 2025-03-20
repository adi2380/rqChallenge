package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

    private String id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;

    @JsonProperty("employee_name")
    public String getName() {
        return name;
    }

    @JsonProperty("employee_name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("employee_salary")
    public Integer getSalary() {
        return salary;
    }

    @JsonProperty("employee_salary")
    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @JsonProperty("employee_age")
    public Integer getAge() {
        return age;
    }

    @JsonProperty("employee_age")
    public void setAge(Integer age) {
        this.age = age;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("employee_title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("employee_title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("employee_email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("employee_email")
    public void setEmail(String email) {
        this.email = email;
    }
}
