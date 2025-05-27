package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MedicalRecordService medicalRecordService;

    @Autowired
    ObjectMapper objectMapper;

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        medicalRecord = MedicalRecord.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate(LocalDate.parse("01/01/1980", formatter))
                .medications(List.of("med1:100mg"))
                .allergies(List.of("pollen"))
                .build();
    }

    // ADD MEDICAL RECORD
    @Test
    void testAddMedicalRecord_created() throws Exception {
        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class)))
                .thenReturn(Optional.of(medicalRecord));
        String body = objectMapper.writeValueAsString(medicalRecord);

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(201));
    }

    @Test
    void testAddMedicalRecord_conflict() throws Exception {
        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().is(409));
    }

    // UPDATE MEDICAL RECORD
    @Test
    void testUpdateMedicalRecord_updated() throws Exception {
        when(medicalRecordService.updateMedicalRecord(any(MedicalRecord.class)))
                .thenReturn(Optional.of(medicalRecord));

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().is(200));
    }

    @Test
    void testUpdateMedicalRecord_notFound() throws Exception {
        when(medicalRecordService.updateMedicalRecord(any(MedicalRecord.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecord)))
                .andExpect(status().is(410));
    }

    // DELETE
    @Test
    void testDeleteMedicalRecord_deleted() throws Exception {
        when(medicalRecordService.deleteMedicalRecord(anyString(), anyString()))
                .thenReturn(true);

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().is(200));
    }

    @Test
    void testDeleteMedicalRecord_notFound() throws Exception {
        when(medicalRecordService.deleteMedicalRecord(anyString(), anyString()))
                .thenReturn(false);

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Ghost")
                        .param("lastName", "Nobody"))
                .andExpect(status().is(410));
    }
}
