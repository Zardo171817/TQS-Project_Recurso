package com.example.demo.controller;

import com.example.demo.entity.Promoter;
import com.example.demo.repository.PromoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromoterController.class)
class PromoterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromoterRepository promoterRepository;

    private Promoter testPromoter;
    private Promoter testPromoter2;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("test@promoter.com");
        testPromoter.setOrganization("Test Organization");

        testPromoter2 = new Promoter();
        testPromoter2.setId(2L);
        testPromoter2.setName("Second Promoter");
        testPromoter2.setEmail("second@promoter.com");
        testPromoter2.setOrganization("Second Organization");
    }

    @Test
    void whenGetAllPromoters_thenReturnList() throws Exception {
        List<Promoter> promoters = Arrays.asList(testPromoter, testPromoter2);
        when(promoterRepository.findAll()).thenReturn(promoters);

        mockMvc.perform(get("/api/promoters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Promoter")))
                .andExpect(jsonPath("$[0].email", is("test@promoter.com")))
                .andExpect(jsonPath("$[0].organization", is("Test Organization")))
                .andExpect(jsonPath("$[1].name", is("Second Promoter")));

        verify(promoterRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllPromotersEmpty_thenReturnEmptyList() throws Exception {
        when(promoterRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/promoters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(promoterRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllPromotersSingleItem_thenReturnSingleItemList() throws Exception {
        List<Promoter> promoters = Collections.singletonList(testPromoter);
        when(promoterRepository.findAll()).thenReturn(promoters);

        mockMvc.perform(get("/api/promoters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Promoter")));

        verify(promoterRepository, times(1)).findAll();
    }
}
