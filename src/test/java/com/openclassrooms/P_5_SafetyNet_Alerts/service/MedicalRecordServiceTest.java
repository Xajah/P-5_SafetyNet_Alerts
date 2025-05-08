package com.openclassrooms.P_5_SafetyNet_Alerts.service;


import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    @Mock
    DataLoader dataLoader;


    MedicalRecordService serviceUnderTest;

    List<MedicalRecord> medicalRecordsMock;

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @BeforeEach
    public void setUp(){


        medicalRecordsMock = Arrays.asList(
                MedicalRecord.builder()
                        .firstName("John")
                        .lastName("Boyd")
                        .birthdate(LocalDate.parse("03/06/1984", formatter))
                        .medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
                        .allergies(Arrays.asList("nillacilan"))
                        .build(),
                MedicalRecord.builder()
                        .firstName("Jacob")
                        .lastName("Boyd")
                        .birthdate(LocalDate.parse("03/06/1989", formatter))
                        .medications(Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"))
                        .allergies(Arrays.asList())
                        .build(),
                MedicalRecord.builder()
                        .firstName("Tenley")
                        .lastName("Boyd")
                        .birthdate(LocalDate.parse("02/18/2012", formatter))
                        .medications(Arrays.asList())
                        .allergies(Arrays.asList("peanut"))
                        .build(),
                MedicalRecord.builder()
                        .firstName("Peter")
                        .lastName("Duncan")
                        .birthdate(LocalDate.parse("09/06/2000", formatter))
                        .medications(Arrays.asList("dodoxadin:30mg"))
                        .allergies(Arrays.asList("shellfish"))
                        .build()
        );
        serviceUnderTest = new MedicalRecordService(dataLoader);
    }
              @Test
            public void testGetMedicalRecordByName_found(){
        //Arrange
                  when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);
                  //Act
                  Optional<MedicalRecord> result = serviceUnderTest.getMedicalRecordByName("John","Boyd");
                  Optional<MedicalRecord> expected = Optional.of(MedicalRecord.builder()
                          .firstName("John")
                          .lastName("Boyd")
                          .birthdate(LocalDate.parse("03/06/1984", formatter))
                          .medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
                          .allergies(Arrays.asList("nillacilan"))
                          .build());
                  //assert
                  assertEquals(result, expected );
              }
    @Test
    public void testGetMedicalRecordByName_noneFound(){
        //Arrange
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);
        //Act
        Optional<MedicalRecord> result = serviceUnderTest.getMedicalRecordByName("Marc","Valerie");

        //assert
        assertEquals(result, Optional.empty());
    }
    @Test
    public void testGetMedicalRecordByName_getMRSnull(){
        //Arrange
        when(dataLoader.getMedicalRecords()).thenReturn(Arrays.asList());
        //Act
        Optional<MedicalRecord> result = serviceUnderTest.getMedicalRecordByName("John","Boyd");

        //Assert
        assertEquals(result,Optional.empty());

    }
    @Test
    public void testGetMedicalRecords(){
        //Arrange
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);
        //Act
        List<MedicalRecord> result = serviceUnderTest.getMedicalRecords();
        MedicalRecord expected1 = MedicalRecord.builder()
                .firstName("John")
                .lastName("Boyd")
                .birthdate(LocalDate.parse("03/06/1984", formatter))
                .medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(Arrays.asList("nillacilan"))
                .build();
        MedicalRecord expected2 = MedicalRecord.builder()
                .firstName("Tenley")
                .lastName("Boyd")
                .birthdate(LocalDate.parse("02/18/2012", formatter))
                .medications(Arrays.asList())
                .allergies(Arrays.asList("peanut"))
                .build();

        assertFalse(result.isEmpty(), "La Liste de Medical Records est vide");
        assertTrue(result.contains(expected1));
        assertTrue(result.contains(expected2));

    }
    @Test
    public void testGetMedicalRecords_dataNull(){
        //Arrange
        when(dataLoader.getMedicalRecords()).thenReturn(null);
        //Act
        List<MedicalRecord> result = serviceUnderTest.getMedicalRecords();
        //Assert
        assertNull(result);
    }
    }

