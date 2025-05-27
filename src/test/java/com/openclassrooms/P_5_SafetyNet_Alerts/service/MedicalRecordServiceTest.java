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
import java.util.ArrayList;
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


        medicalRecordsMock = new ArrayList<>(Arrays.asList(
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
        ));
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
    //--- ADD

    @Test
    void testAddMedicalRecord_Added() {
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);

        MedicalRecord record = MedicalRecord.builder()
                .firstName("Rick").lastName("Sanchez")
                .birthdate(LocalDate.parse("12/05/1967", formatter))
                .medications(Arrays.asList("alcohol:lots"))
                .allergies(Arrays.asList("none")).build();

        Optional<MedicalRecord> result = serviceUnderTest.addMedicalRecord(record);

        assertNotNull(result);
        assertEquals("Rick", result.get().getFirstName());
        assertEquals("Sanchez", result.get().getLastName());
    }

    @Test
    void testAddMedicalRecord_NotAdded_AlreadyExists() {
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);
        // Already present
        MedicalRecord record = MedicalRecord.builder()
                .firstName("John").lastName("Boyd")
                .birthdate(LocalDate.parse("01/01/2000", formatter))
                .medications(Arrays.asList()).allergies(Arrays.asList()).build();

        Optional<MedicalRecord> result = serviceUnderTest.addMedicalRecord(record);

        assertTrue(result.isEmpty());
    }

    @Test
    void testAddMedicalRecord_NoData() {
        when(dataLoader.getMedicalRecords()).thenReturn(new ArrayList<>());
        MedicalRecord record = MedicalRecord.builder()
                .firstName("Summer").lastName("Smith")
                .birthdate(LocalDate.parse("02/14/2003", formatter))
                .build();

        Optional<MedicalRecord> result = serviceUnderTest.addMedicalRecord(record);

        assertTrue(result.isEmpty());
    }

    @Test
    void testAddMedicalRecord_NullFirstnameOrLastname() {

        MedicalRecord record = MedicalRecord.builder().firstName(null).lastName("Who").build();
        Optional<MedicalRecord> result = serviceUnderTest.addMedicalRecord(record);

        assertTrue(result.isEmpty());

        record = MedicalRecord.builder().firstName("Who").lastName(null).build();
        result = serviceUnderTest.addMedicalRecord(record);

        assertTrue(result.isEmpty());
    }

//--- UPDATE

    @Test
    void testUpdateMedicalRecord_Success() {
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);

        MedicalRecord update = MedicalRecord.builder()
                .firstName("Tenley").lastName("Boyd")
                .birthdate(LocalDate.parse("12/31/2020", formatter))
                .medications(Arrays.asList("ibuprofen:200mg"))
                .allergies(Arrays.asList("dust"))
                .build();

        Optional<MedicalRecord> result = serviceUnderTest.updateMedicalRecord(update);

        assertTrue(result.isPresent());
        assertEquals(LocalDate.parse("12/31/2020", formatter), result.get().getBirthdate());
        assertEquals(Arrays.asList("ibuprofen:200mg"), result.get().getMedications());
        assertEquals(Arrays.asList("dust"), result.get().getAllergies());
    }

    @Test
    void testUpdateMedicalRecord_EmptyList() {
        when(dataLoader.getMedicalRecords()).thenReturn(new ArrayList<>());
        MedicalRecord update = MedicalRecord.builder().firstName("Nobody").lastName("Unknown").build();

        Optional<MedicalRecord> result = serviceUnderTest.updateMedicalRecord(update);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateMedicalRecord_NotFound() {
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);

        MedicalRecord update = MedicalRecord.builder().firstName("Alien").lastName("Invader").build();

        Optional<MedicalRecord> result = serviceUnderTest.updateMedicalRecord(update);

        assertTrue(result.isEmpty());
    }

//--- DELETE

    @Test
    void testDeleteMedicalRecord_Success() {
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);
        int sizeBefore = medicalRecordsMock.size();

        boolean result = serviceUnderTest.deleteMedicalRecord("Jacob", "Boyd");

        assertTrue(result);
        assertEquals(sizeBefore - 1, medicalRecordsMock.size());
        assertTrue(medicalRecordsMock.stream().noneMatch(r -> r.getFirstName().equals("Jacob") && r.getLastName().equals("Boyd")));
    }

    @Test
    void testDeleteMedicalRecord_NotFound() {
        when(dataLoader.getMedicalRecords()).thenReturn(medicalRecordsMock);
        int sizeBefore = medicalRecordsMock.size();

        boolean result = serviceUnderTest.deleteMedicalRecord("Not", "Exist");

        assertFalse(result);
        assertEquals(sizeBefore, medicalRecordsMock.size());

    }

}

