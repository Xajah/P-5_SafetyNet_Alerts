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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestFirestationController_IT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataLoader dataLoader;

    private static final String ORIGINAL_DATA_PATH = "/Data/data-original.json";
    private static final String WORKING_DATA_PATH = "Data/data.json";

    @BeforeEach
    void restoreDataFileBeforeEachTest() throws Exception {

        try (InputStream is = getClass().getResourceAsStream(ORIGINAL_DATA_PATH)) {
            if (is == null) throw new RuntimeException("data-original.json missing from src/test/resources/Data/");
            Files.copy(is, Path.of(WORKING_DATA_PATH), StandardCopyOption.REPLACE_EXISTING);
        }

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
        dataLoader.run();
        boolean exists = dataLoader.getFirestations()
                .stream()
                .anyMatch(fire -> "100 New St".equalsIgnoreCase(fire.getAddress()) && fire.getStation() == 7);

        assertTrue(exists, "La nouvelle caserne devrait être présente dans les données persistées");
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
