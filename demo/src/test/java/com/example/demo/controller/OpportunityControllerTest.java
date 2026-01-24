package com.example.demo.controller;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.dto.UpdateOpportunityRequest;
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

    // Update Opportunity Tests
    @Test
    void whenUpdateOpportunity_thenReturnUpdatedOpportunity() throws Exception {
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle("Updated Opportunity");
        updateRequest.setDescription("Updated Description for opportunity");
        updateRequest.setSkills("Java, Spring, Docker");
        updateRequest.setCategory("Tecnologia");
        updateRequest.setDuration(20);
        updateRequest.setVacancies(10);
        updateRequest.setPoints(200);

        OpportunityResponse updatedResponse = new OpportunityResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Opportunity");
        updatedResponse.setDescription("Updated Description for opportunity");
        updatedResponse.setSkills("Java, Spring, Docker");
        updatedResponse.setCategory("Tecnologia");
        updatedResponse.setDuration(20);
        updatedResponse.setVacancies(10);
        updatedResponse.setPoints(200);
        updatedResponse.setPromoterId(1L);
        updatedResponse.setPromoterName("Test Promoter");
        updatedResponse.setCreatedAt(LocalDateTime.now());

        when(opportunityService.updateOpportunity(eq(1L), any(UpdateOpportunityRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/opportunities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Opportunity")))
                .andExpect(jsonPath("$.duration", is(20)))
                .andExpect(jsonPath("$.vacancies", is(10)))
                .andExpect(jsonPath("$.points", is(200)));

        verify(opportunityService, times(1)).updateOpportunity(eq(1L), any(UpdateOpportunityRequest.class));
    }

    @Test
    void whenUpdateOpportunityNotFound_thenReturnNotFound() throws Exception {
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        updateRequest.setTitle("Updated Opportunity");
        updateRequest.setDescription("Updated Description for opportunity");
        updateRequest.setSkills("Java, Spring");
        updateRequest.setCategory("Tecnologia");
        updateRequest.setDuration(20);
        updateRequest.setVacancies(10);
        updateRequest.setPoints(200);

        when(opportunityService.updateOpportunity(eq(999L), any(UpdateOpportunityRequest.class)))
                .thenThrow(new ResourceNotFoundException("Opportunity not found with id: 999"));

        mockMvc.perform(put("/api/opportunities/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateOpportunityWithInvalidData_thenReturnBadRequest() throws Exception {
        UpdateOpportunityRequest invalidRequest = new UpdateOpportunityRequest();
        invalidRequest.setTitle(""); // Invalid - blank

        mockMvc.perform(put("/api/opportunities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Delete Opportunity Tests
    @Test
    void whenDeleteOpportunity_thenReturnNoContent() throws Exception {
        doNothing().when(opportunityService).deleteOpportunity(1L);

        mockMvc.perform(delete("/api/opportunities/1"))
                .andExpect(status().isNoContent());

        verify(opportunityService, times(1)).deleteOpportunity(1L);
    }

    @Test
    void whenDeleteOpportunityNotFound_thenReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Opportunity not found with id: 999"))
                .when(opportunityService).deleteOpportunity(999L);

        mockMvc.perform(delete("/api/opportunities/999"))
                .andExpect(status().isNotFound());
    }
}
