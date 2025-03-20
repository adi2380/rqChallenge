package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.exceptions.ResourceNotFoundException;
import com.reliaquest.api.model.ApiException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee sampleEmployee;
    private List<Employee> sampleEmployees;

    @BeforeEach
    void setUp() {
        sampleEmployee = new Employee();
        sampleEmployee.setId(UUID.randomUUID().toString());
        sampleEmployee.setName("John Doe");
        sampleEmployee.setSalary(100000);
        sampleEmployee.setAge(30);

        Employee employee2 = new Employee();
        employee2.setId(UUID.randomUUID().toString());
        employee2.setName("Jane Doe");
        employee2.setSalary(120000);
        employee2.setAge(28);

        sampleEmployees = Arrays.asList(sampleEmployee, employee2);
    }

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        when(employeeService.getAllEmployees()).thenReturn(sampleEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnMatchingEmployees() {
        when(employeeService.getEmployeesByNameSearch(anyString())).thenReturn(sampleEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("Doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getEmployeeById_shouldReturnEmployee_whenFound() {
        when(employeeService.getEmployeeById(sampleEmployee.getId())).thenReturn(sampleEmployee);

        ResponseEntity<Employee> response = employeeController.getEmployeeById(sampleEmployee.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleEmployee.getId(), response.getBody().getId());
    }

    @Test
    void getEmployeeById_shouldReturnNotFound_whenEmployeeNotFound() {
        String id = UUID.randomUUID().toString();
        when(employeeService.getEmployeeById(id)).thenThrow(new ApiException("Employee not found"));

        assertThrows(ApiException.class, () -> employeeController.getEmployeeById(id));
    }

    @Test
    void getHighestSalaryOfEmployees_shouldReturnHighestSalary() {

        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(120000);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(120000, response.getBody());
    }

    @Test
    void getTop10HighestEarningEmployeeNames_shouldReturnTopEarners() {
        List<String> topEarners = Arrays.asList("Jane Doe", "John Doe");
        when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(topEarners);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Jane Doe", response.getBody().get(0));
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee_whenSuccessful() {
        EmployeeRequest newEmployee = new EmployeeRequest();
        newEmployee.setName("New Employee");

        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(sampleEmployee);

        ResponseEntity<Employee> response = employeeController.createEmployee(newEmployee);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleEmployee.getId(), response.getBody().getId());
    }

    @Test
    void createEmployee_shouldReturnBadRequest_whenCreationFails() {

        EmployeeRequest newEmployee = new EmployeeRequest();
        when(employeeService.createEmployee(any(EmployeeRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid Field Value"));

        assertThrows(IllegalArgumentException.class, () -> employeeController.createEmployee(newEmployee));
    }

    @Test
    void deleteEmployeeById_shouldReturnEmployeeName_whenSuccessful() {
        when(employeeService.deleteEmployeeById(sampleEmployee.getId())).thenReturn("John Doe");

        ResponseEntity<String> response = employeeController.deleteEmployeeById(sampleEmployee.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody());
    }

    @Test
    void deleteEmployeeById_shouldReturnNotFound_whenEmployeeNotFound() {
        String id = UUID.randomUUID().toString();
        when(employeeService.deleteEmployeeById(id)).thenThrow(new ResourceNotFoundException("Employee not found"));

        assertThrows(ResourceNotFoundException.class, () -> employeeController.deleteEmployeeById(id));
    }
}
