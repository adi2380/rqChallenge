package com.reliaquest.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.EmployeeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateEmployee_InvalidRequest() throws Exception {
        EmployeeRequest invalidRequest = new EmployeeRequest();
        invalidRequest.setAge(88);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name cannot be null or blank"))
                .andExpect(jsonPath("$.salary").value("Salary cannot be empty"))
                .andExpect(jsonPath("$.age").value("Age must be between 16 and 75"))
                .andExpect(jsonPath("$.title").value("Title cannot be null or empty"));
    }

    @Test
    public void testGetEmployeeById_employeeIsNotUUID() throws Exception {

        mockMvc.perform(get("/api/v1/employee/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteEmployeeById_employeeIsNotUUID() throws Exception {

        mockMvc.perform(delete("/api/v1/employee/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
