package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.FirestationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FirestationController.class)
public class FirestationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FirestationService firestationService;

    @Autowired
    ObjectMapper objectMapper;

    private Firestation firestation;

    @BeforeEach
    void setUp() {
        firestation = Firestation.builder()
                .address("28 W 29th St")
                .station(3)
                .build();
    }

    // ADD FIRESTATION
    @Test
    void testAddFirestation_created() throws Exception {
        when(firestationService.addFirestation(any(Firestation.class)))
                .thenReturn(Optional.of(firestation));

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firestation)))
                .andExpect(status().is(201));
    }

    @Test
    void testAddFirestation_conflict() throws Exception {
        when(firestationService.addFirestation(any(Firestation.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firestation)))
                .andExpect(status().is(409));
    }

    // UPDATE FIRESTATION
    @Test
    void testUpdateFirestation_updated() throws Exception {
        when(firestationService.updateFirestation(any(Firestation.class)))
                .thenReturn(Optional.of(firestation));

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firestation)))
                .andExpect(status().is(200));
    }

    @Test
    void testUpdateFirestation_notFound() throws Exception {
        when(firestationService.updateFirestation(any(Firestation.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firestation)))
                .andExpect(status().is(410));
    }

    // DELETE BY ADDRESS
    @Test
    void testDeleteFirestationByAddress_deleted() throws Exception {
        when(firestationService.deleteFirestationMappingByAdress(anyString()))
                .thenReturn(true);

        mockMvc.perform(delete("/firestation")
                        .param("address", "28 W 29th St"))
                .andExpect(status().is(200));
    }

    @Test
    void testDeleteFirestationByAddress_notFound() throws Exception {
        when(firestationService.deleteFirestationMappingByAdress(anyString()))
                .thenReturn(false);

        mockMvc.perform(delete("/firestation")
                        .param("address", "inconnue"))
                .andExpect(status().is(410));
    }

    // DELETE BY ID
    @Test
    void testDeleteFirestationById_deleted() throws Exception {
        when(firestationService.deleteFirestationMappingById(anyInt()))
                .thenReturn(true);

        mockMvc.perform(delete("/firestation")
                        .param("firestation", "3"))
                .andExpect(status().is(200));
    }

    @Test
    void testDeleteFirestationById_notFound() throws Exception {
        when(firestationService.deleteFirestationMappingById(anyInt()))
                .thenReturn(false);

        mockMvc.perform(delete("/firestation")
                        .param("firestation", "99"))
                .andExpect(status().is(410));
    }
}
