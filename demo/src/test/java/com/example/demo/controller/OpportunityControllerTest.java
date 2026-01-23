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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
        testResponse.setDescription("Test Description for opportunity");
        testResponse.setSkills("Java, Spring");
        testResponse.setCategory("Tecnologia");
        testResponse.setDuration(10);
        testResponse.setVacancies(5);
        testResponse.setPoints(100);
        testResponse.setPromoterId(1L);
        testResponse.setPromoterName("Test Promoter");
        testResponse.setCreatedAt(LocalDateTime.now());

        validRequest = new CreateOpportunityRequest();
        validRequest.setTitle("New Opportunity");
        validRequest.setDescription("Description for new opportunity");
        validRequest.setSkills("Python, Django");
        validRequest.setCategory("Tecnologia");
        validRequest.setDuration(15);
        validRequest.setVacancies(3);
        validRequest.setPoints(150);
        validRequest.setPromoterId(1L);
    }

    @Test
    void whenCreateOpportunity_thenReturnCreated() throws Exception {
        when(opportunityService.createOpportunity(any(CreateOpportunityRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Opportunity")));

        verify(opportunityService, times(1)).createOpportunity(any(CreateOpportunityRequest.class));
    }

    @Test
    void whenCreateOpportunityWithInvalidData_thenReturnBadRequest() throws Exception {
        CreateOpportunityRequest invalidRequest = new CreateOpportunityRequest();
        invalidRequest.setTitle(""); // Invalid - blank

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOpportunityById_thenReturnOpportunity() throws Exception {
        when(opportunityService.getOpportunityById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/opportunities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Opportunity")));

        verify(opportunityService, times(1)).getOpportunityById(1L);
    }

    @Test
    void whenGetOpportunityByIdNotFound_thenReturnNotFound() throws Exception {
        when(opportunityService.getOpportunityById(999L))
                .thenThrow(new ResourceNotFoundException("Opportunity not found with id: 999"));

        mockMvc.perform(get("/api/opportunities/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllOpportunities_thenReturnList() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.getAllOpportunities()).thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Opportunity")));

        verify(opportunityService, times(1)).getAllOpportunities();
    }

    @Test
    void whenGetAllOpportunitiesEmpty_thenReturnEmptyList() throws Exception {
        when(opportunityService.getAllOpportunities()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetOpportunitiesByPromoter_thenReturnList() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.getOpportunitiesByPromoter(1L)).thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities/promoter/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(opportunityService, times(1)).getOpportunitiesByPromoter(1L);
    }

    @Test
    void whenGetOpportunitiesByPromoterNotFound_thenReturnNotFound() throws Exception {
        when(opportunityService.getOpportunitiesByPromoter(999L))
                .thenThrow(new ResourceNotFoundException("Promoter not found with id: 999"));

        mockMvc.perform(get("/api/opportunities/promoter/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenFilterOpportunities_thenReturnFilteredList() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.filterOpportunitiesByParams(
                eq("Tecnologia"), eq(null), eq(null), eq(null)))
                .thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("category", "Tecnologia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Tecnologia")));
    }

    @Test
    void whenFilterOpportunitiesBySkills_thenReturnFilteredList() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.filterOpportunitiesByParams(
                eq(null), eq("Java"), eq(null), eq(null)))
                .thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("skills", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenFilterOpportunitiesByDuration_thenReturnFilteredList() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.filterOpportunitiesByParams(
                eq(null), eq(null), eq(5), eq(15)))
                .thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("minDuration", "5")
                        .param("maxDuration", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenFilterOpportunitiesWithAllParams_thenReturnFilteredList() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.filterOpportunitiesByParams(
                eq("Tecnologia"), eq("Java"), eq(5), eq(20)))
                .thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities/filter")
                        .param("category", "Tecnologia")
                        .param("skills", "Java")
                        .param("minDuration", "5")
                        .param("maxDuration", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenFilterOpportunitiesNoParams_thenReturnAllOpportunities() throws Exception {
        List<OpportunityResponse> opportunities = Arrays.asList(testResponse);
        when(opportunityService.filterOpportunitiesByParams(
                eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(opportunities);

        mockMvc.perform(get("/api/opportunities/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void whenGetAllCategories_thenReturnCategoryList() throws Exception {
        List<String> categories = Arrays.asList("Tecnologia", "Saude", "Educacao");
        when(opportunityService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/opportunities/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Tecnologia")));

        verify(opportunityService, times(1)).getAllCategories();
    }

    @Test
    void whenGetAllCategoriesEmpty_thenReturnEmptyList() throws Exception {
        when(opportunityService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/opportunities/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
