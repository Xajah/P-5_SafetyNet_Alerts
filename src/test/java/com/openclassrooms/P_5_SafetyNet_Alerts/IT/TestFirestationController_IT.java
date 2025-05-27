package com.openclassrooms.P_5_SafetyNet_Alerts.IT;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TestFirestationController_IT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataLoader dataLoader;

    @BeforeEach
    void resetDataLoader () throws Exception{
        dataLoader.run();
    }

    @Test
    void testAddFirestation_ok() throws Exception {
        Firestation f = Firestation.builder()
                .address("100 New St")
                .station(7)
                .build();

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("100 New St"))
                .andExpect(jsonPath("$.station").value(7));
    }

    @Test
    void testAddFirestation_conflict() throws Exception {
        // Firestation already present in dataset (ex: 1509 Culver St, station 3)
        Firestation f = Firestation.builder()
                .address("1509 Culver St")
                .station(3)
                .build();

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateFirestation_found() throws Exception {
        // MAJ sur une firestation déjà existante
        Firestation f = Firestation.builder()
                .address("1509 Culver St")
                .station(7)
                .build();

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value(7));
    }

    @Test
    void testUpdateFirestation_notFound() throws Exception {
        Firestation f = Firestation.builder()
                .address("Invisible St")
                .station(1)
                .build();

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isGone());
    }

    @Test
    void testDeleteFirestation_byAddress_found() throws Exception {
        mockMvc.perform(delete("/firestation")
                        .param("address", "748 Townings Dr"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFirestation_byAddress_notFound() throws Exception {
        mockMvc.perform(delete("/firestation")
                        .param("address", "Unknown Street"))
                .andExpect(status().isGone());
    }

    @Test
    void testDeleteFirestation_byId_found() throws Exception {
        // Suppose id=3 existe
        mockMvc.perform(delete("/firestation")
                        .param("firestation", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFirestation_byId_notFound() throws Exception {
        mockMvc.perform(delete("/firestation")

                        .param("firestation", "99"))
                .andExpect(status().isGone());
    }

}

