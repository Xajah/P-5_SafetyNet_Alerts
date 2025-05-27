package com.openclassrooms.P_5_SafetyNet_Alerts.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TestMedicalRecordController_IT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    DataLoader dataLoader;

    @BeforeEach
    void resetDataLoader () throws Exception{
        dataLoader.run();
    }


    @Test
    void testAddMedicalRecord_ok() throws Exception {
        MedicalRecord m = MedicalRecord.builder()
                .firstName("New")
                .lastName("Patient")
                .birthdate(LocalDate.parse("01/01/2020", formatter))
                .medications(List.of("vitaminD:200mg"))
                .allergies(List.of("pollen"))
                .build();

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Patient"));
    }

    @Test
    void testAddMedicalRecord_conflict() throws Exception {
        // John Boyd already exists
        MedicalRecord m = MedicalRecord.builder()
                .firstName("John")
                .lastName("Boyd")
                .birthdate(LocalDate.parse("12/06/1975", formatter))
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateMedicalRecord_found() throws Exception {
        // Mise à jour du medical record pour Brian Stelzer
        MedicalRecord m = MedicalRecord.builder()
                .firstName("Brian")
                .lastName("Stelzer")
                .birthdate(LocalDate.parse("12/06/1975", formatter))
                .medications(List.of("paracetamol:500mg"))
                .allergies(List.of("lactose"))
                .build();

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Brian"))
                .andExpect(jsonPath("$.medications[0]").value("paracetamol:500mg"));
    }

    @Test
    void testUpdateMedicalRecord_notFound() throws Exception {
        MedicalRecord m = MedicalRecord.builder()
                .firstName("Ghost")
                .lastName("Person")
                .birthdate(LocalDate.parse("05/05/2000", formatter))
                .medications(List.of())
                .allergies(List.of())
                .build();

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isGone());
    }

    @Test
    void testDeleteMedicalRecord_found() throws Exception {
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Allison")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMedicalRecord_notFound() throws Exception {
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Nobody")
                        .param("lastName", "Here"))
                .andExpect(status().isGone());
    }
}

