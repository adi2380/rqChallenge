package com.reliaquest.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exceptions.ResourceNotFoundException;
import com.reliaquest.api.model.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Value("${api.employee.baseUrl:http://localhost:8112/api/v1/employee}")
    private String employeeApiBaseUrl;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final ObjectMapper objectMapper;

    public List<Employee> getAllEmployees() {
        logger.debug("Fetching all employees from API");
        ResponseEntity<EmployeeServiceResponse> response =
                restTemplate.getForEntity(employeeApiBaseUrl, EmployeeServiceResponse.class);
        return objectMapper.convertValue(handleResponse(response).getData(), new TypeReference<List<Employee>>() {});
    }

    public List<Employee> getEmployeesByNameSearch(String nameFragment) {
        logger.debug("Searching employees with name fragment: {}", nameFragment);
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(employee -> employee.getName() != null
                        && employee.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        logger.info("Fetching employee with ID: {}", id);
        String url = String.format("%s/%s", employeeApiBaseUrl, id);
        ResponseEntity<EmployeeServiceResponse> response;
        try {
            response = restTemplate.getForEntity(url, EmployeeServiceResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error occurred in geting employee data by id  cause : {}", e);
            throw new ApiException(
                    "Error occurred in getting employee data by id  cause : {}",
                    e.getStatusCode().value());
        }
        return objectMapper.convertValue(handleResponse(response).getData(), new TypeReference<Employee>() {});
    }

    public Integer getHighestSalaryOfEmployees() {
        logger.debug("Finding highest salary among employees");
        List<Employee> employees = getAllEmployees();
        return employees.stream().mapToInt(Employee::getSalary).max().orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        logger.debug("Finding top 10 highest earning employees");
        List<Employee> employees = getAllEmployees();
        return employees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(EmployeeRequest employee) {
        logger.debug("Creating a new employee");
        ResponseEntity<EmployeeServiceResponse> response =
                restTemplate.postForEntity(employeeApiBaseUrl, employee, EmployeeServiceResponse.class);
        return objectMapper.convertValue(handleResponse(response).getData(), new TypeReference<Employee>() {});
    }

    public String deleteEmployeeById(String id) {
        logger.info("Deleting employee with ID: {}", id);
        // First get the employee to return their name
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            logger.error("Employee Not found with Id : {}", id);
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }

        ResponseEntity<EmployeeServiceResponse> response = restTemplate.exchange(
                employeeApiBaseUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(Map.entry("name", employee.getName())),
                EmployeeServiceResponse.class);

        if ((boolean) handleResponse(response).getData()) {
            return employee.getName();
        } else {
            logger.error("Unable to delete resource with name : {}", employee.getName());
            throw new RuntimeException("Unable to delete resource with name : " + employee.getName());
        }
    }

    private EmployeeServiceResponse handleResponse(ResponseEntity<EmployeeServiceResponse> response) {
        logger.info("Response received : {}", response);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else if (response.getStatusCode().value() == 404) {
            logger.error("Failed to find resource, Please check the input provided");
            throw new ResourceNotFoundException("Failed to find resource, Please check the input provided");
        } else {
            logger.error("Failed to get employee: {}", response.getStatusCode());
            throw new ApiException("Failed to get employee: " + response.getStatusCode());
        }
    }
}
