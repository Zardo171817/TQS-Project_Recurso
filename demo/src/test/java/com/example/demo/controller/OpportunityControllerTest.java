package com.example.demo.controller;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.OpportunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpportunityController.class)
class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpportunityService opportunityService;

    private OpportunityResponse testResponse;
    private CreateOpportunityRequest validRequest;

    @BeforeEach
    void setUp() {
        testResponse = new OpportunityResponse();
        testResponse.setId(1L);
        testResponse.setTitle("Test Opportunity");
        testResponse.setDescription("This is a test opportunity");
        testResponse.setSkills("Java, Spring Boot");
        testResponse.setDuration(10);
        testResponse.setVacancies(5);
        testResponse.setPoints(100);
        testResponse.setPromoterId(1L);
        testResponse.setPromoterName("Test Promoter");
        testResponse.setCreatedAt(LocalDateTime.now());

        validRequest = new CreateOpportunityRequest();
        validRequest.setTitle("Test Opportunity");
        validRequest.setDescription("This is a test opportunity");
        validRequest.setSkills("Java, Spring Boot");
        validRequest.setDuration(10);
        validRequest.setVacancies(5);
        validRequest.setPoints(100);
        validRequest.setPromoterId(1L);
    }

    @Test
    void whenCreateOpportunityWithValidData_thenReturn201() throws Exception {
        when(opportunityService.createOpportunity(any(CreateOpportunityRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Opportunity"))
                .andExpect(jsonPath("$.description").value("This is a test opportunity"))
                .andExpect(jsonPath("$.skills").value("Java, Spring Boot"))
                .andExpect(jsonPath("$.duration").value(10))
                .andExpect(jsonPath("$.vacancies").value(5))
                .andExpect(jsonPath("$.points").value(100))
                .andExpect(jsonPath("$.promoterId").value(1L))
                .andExpect(jsonPath("$.promoterName").value("Test Promoter"));
    }

    @Test
    void whenCreateOpportunityWithBlankTitle_thenReturn400() throws Exception {
        validRequest.setTitle("");

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void whenCreateOpportunityWithNullDuration_thenReturn400() throws Exception {
        validRequest.setDuration(null);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.duration").exists());
    }

    @Test
    void whenCreateOpportunityWithNegativePoints_thenReturn400() throws Exception {
        validRequest.setPoints(-10);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.points").exists());
    }

    @Test
    void whenCreateOpportunityWithShortDescription_thenReturn400() throws Exception {
        validRequest.setDescription("short");

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    void whenGetOpportunityByIdWithValidId_thenReturn200() throws Exception {
        when(opportunityService.getOpportunityById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/opportunities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Opportunity"));
    }

    @Test
    void whenGetOpportunityByIdWithInvalidId_thenReturn404() throws Exception {
        when(opportunityService.getOpportunityById(999L))
                .thenThrow(new ResourceNotFoundException("Opportunity not found with id: 999"));

        mockMvc.perform(get("/api/opportunities/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Opportunity not found with id: 999"));
    }

    @Test
    void whenGetAllOpportunities_thenReturn200() throws Exception {
        OpportunityResponse response2 = new OpportunityResponse();
        response2.setId(2L);
        response2.setTitle("Second Opportunity");
        response2.setDescription("Second description");
        response2.setSkills("Python, Django");
        response2.setDuration(20);
        response2.setVacancies(3);
        response2.setPoints(150);
        response2.setPromoterId(1L);
        response2.setPromoterName("Test Promoter");
        response2.setCreatedAt(LocalDateTime.now());

        List<OpportunityResponse> responses = Arrays.asList(testResponse, response2);
        when(opportunityService.getAllOpportunities()).thenReturn(responses);

        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Test Opportunity"))
                .andExpect(jsonPath("$[1].title").value("Second Opportunity"));
    }

    @Test
    void whenGetOpportunitiesByPromoterWithValidId_thenReturn200() throws Exception {
        List<OpportunityResponse> responses = Arrays.asList(testResponse);
        when(opportunityService.getOpportunitiesByPromoter(1L)).thenReturn(responses);

        mockMvc.perform(get("/api/opportunities/promoter/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].promoterId").value(1L));
    }

    @Test
    void whenGetOpportunitiesByPromoterWithInvalidId_thenReturn404() throws Exception {
        when(opportunityService.getOpportunitiesByPromoter(999L))
                .thenThrow(new ResourceNotFoundException("Promoter not found with id: 999"));

        mockMvc.perform(get("/api/opportunities/promoter/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Promoter not found with id: 999"));
    }
}
