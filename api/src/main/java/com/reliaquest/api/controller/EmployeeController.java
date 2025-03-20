package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeRequest;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeRequest> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Request received to get all employees");
        List<com.reliaquest.api.model.Employee> employees = employeeService.getAllEmployees();
        logger.info("Returning {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        logger.info("Request received to search employees by name fragment: {}", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        logger.info("Found {} employees matching name fragment: {}", employees.size(), searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Request received to get employee by ID: {}", id);

        isValidUUID(id);
        Employee employee = employeeService.getEmployeeById(id);
        logger.info("Found employee with ID: {}", id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Request received to get highest salary among employees");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        logger.info("Highest salary found: {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Request received to get top 10 highest earning employee names");
        List<String> topEarners = employeeService.getTop10HighestEarningEmployeeNames();
        logger.info("Returning {} top earning employees", topEarners.size());
        return ResponseEntity.ok(topEarners);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest employeeInput) {
        logger.info("Request received to create a new employee");

        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        logger.info("Employee created successfully with ID: {}", createdEmployee.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {

        logger.info("Request received to delete an employee record");
        isValidUUID(id);
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        logger.info("Employee deleted successfully with ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedEmployeeName);
    }

    private static void isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please provide valid UUID as employee ID");
        }
    }
}
