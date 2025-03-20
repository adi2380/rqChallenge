package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmployeeRequest {

    @NotBlank(message = "Name cannot be null or blank")
    @JsonProperty
    private String name;

    @NotNull(message = "Salary cannot be empty") @JsonProperty
    private Integer salary;

    @NotNull(message = "Age cannot be empty") @Min(value = 16, message = "Age must be between 16 and 75")
    @Max(value = 75, message = "Age must be between 16 and 75")
    @JsonProperty
    private Integer age;

    @NotBlank(message = "Title cannot be null or empty")
    @JsonProperty
    private String title;

    public @NotBlank String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
