package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeRequest;
import com.reliaquest.api.model.EmployeeServiceResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(employeeService, "employeeApiBaseUrl", "http://localhost:8112/api/v1/employee");
        ReflectionTestUtils.setField(employeeService, "objectMapper", new ObjectMapper());
    }

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        List<Employee> employees = createSampleEmployeeList();
        EmployeeServiceResponse apiResponse = getEmployeeServiceResponse(employees, "Success");
        when(restTemplate.getForEntity(anyString(), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(3, result.size());
        verify(restTemplate).getForEntity("http://localhost:8112/api/v1/employee", EmployeeServiceResponse.class);
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnMatchingEmployees() {
        List<Employee> employees = createSampleEmployeeList();
        EmployeeServiceResponse apiResponse = getEmployeeServiceResponse(employees, "Success");

        when(restTemplate.getForEntity(anyString(), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        List<Employee> result = employeeService.getEmployeesByNameSearch("Doe");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getName().contains("Doe")));
    }

    @Test
    void getEmployeeById_shouldReturnSingleEmployee() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");

        EmployeeServiceResponse apiResponse = getEmployeeServiceResponse(employee, "Success");

        when(restTemplate.getForEntity(anyString(), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        Employee result = employeeService.getEmployeeById("1");

        assertEquals("1", result.getId());
        assertEquals("John Doe", result.getName());
        verify(restTemplate).getForEntity("http://localhost:8112/api/v1/employee/1", EmployeeServiceResponse.class);
    }

    @Test
    void getHighestSalaryOfEmployees_shouldReturnHighestSalary() {
        List<Employee> employees = createSampleEmployeeList();
        EmployeeServiceResponse apiResponse = getEmployeeServiceResponse(employees, "Success");

        when(restTemplate.getForEntity(anyString(), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        Integer result = employeeService.getHighestSalaryOfEmployees();

        assertEquals(120000, result);
    }

    @Test
    void getTop10HighestEarningEmployeeNames_shouldReturnTopEarners() {

        List<Employee> employees = createSampleEmployeeList();
        EmployeeServiceResponse apiResponse = getEmployeeServiceResponse(employees, "Success");

        when(restTemplate.getForEntity(anyString(), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(apiResponse));

        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();

        assertEquals(3, result.size());
        assertEquals("Jane Doe", result.get(0)); // Highest salary
        assertEquals("John Doe", result.get(1)); // Second highest
        assertEquals("Alice Smith", result.get(2)); // Third highest
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() {
        EmployeeRequest newEmployee = new EmployeeRequest();
        newEmployee.setName("New Employee");
        newEmployee.setSalary(90000);
        newEmployee.setAge(25);

        Employee createdEmployee = new Employee();
        createdEmployee.setId("4");
        createdEmployee.setName("New Employee");
        createdEmployee.setSalary(90000);
        createdEmployee.setAge(25);

        EmployeeServiceResponse employeeServiceResponse = getEmployeeServiceResponse(createdEmployee, "Status");

        when(restTemplate.postForEntity(anyString(), any(EmployeeRequest.class), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(employeeServiceResponse));

        Employee result = employeeService.createEmployee(newEmployee);

        assertEquals("4", result.getId());
        assertEquals("New Employee", result.getName());
        verify(restTemplate)
                .postForEntity("http://localhost:8112/api/v1/employee", newEmployee, EmployeeServiceResponse.class);
    }

    @Test
    void deleteEmployeeById_shouldReturnEmployeeName() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");

        EmployeeServiceResponse response = getEmployeeServiceResponse(employee, "Success");
        when(restTemplate.getForEntity(anyString(), eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        EmployeeServiceResponse employeeServiceResponse = getEmployeeServiceResponse(true, "Success");

        when(restTemplate.exchange(
                        anyString(),
                        eq(HttpMethod.DELETE),
                        eq(new HttpEntity<>(Map.entry("name", employee.getName()))),
                        eq(EmployeeServiceResponse.class)))
                .thenReturn(ResponseEntity.ok(employeeServiceResponse));

        String result = employeeService.deleteEmployeeById("1");

        assertEquals("John Doe", result);
        EmployeeRequest request = new EmployeeRequest();
        request.setName(employee.getName());
        verify(restTemplate)
                .exchange(
                        "http://localhost:8112/api/v1/employee",
                        HttpMethod.DELETE,
                        new HttpEntity<>(Map.entry("name", employee.getName())),
                        EmployeeServiceResponse.class);
    }

    private List<Employee> createSampleEmployeeList() {
        Employee employee1 = new Employee();
        employee1.setId("1");
        employee1.setName("John Doe");
        employee1.setSalary(100000);
        employee1.setAge(30);

        Employee employee2 = new Employee();
        employee2.setId("2");
        employee2.setName("Jane Doe");
        employee2.setSalary(120000);
        employee2.setAge(28);

        Employee employee3 = new Employee();
        employee3.setId("3");
        employee3.setName("Alice Smith");
        employee3.setSalary(90000);
        employee3.setAge(25);

        return Arrays.asList(employee1, employee2, employee3);
    }

    public EmployeeServiceResponse getEmployeeServiceResponse(Object data, String status) {
        EmployeeServiceResponse employeeServiceResponse = new EmployeeServiceResponse();
        employeeServiceResponse.setData(data);
        employeeServiceResponse.setStatus(status);
        return employeeServiceResponse;
    }
}
