package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirestationServiceTest {

    @Mock
    private DataLoader dataLoader;

    private FirestationService serviceUnderTest;

    private List<Firestation> firestationsMock;

    @BeforeEach
    void setUp() {
        firestationsMock = Arrays.asList(
                Firestation.builder().address("1509 Culver St").station(1).build(),
                Firestation.builder().address("29 15th St").station(2).build(),
                Firestation.builder().address("834 Binoc Ave").station(3).build(),
                Firestation.builder().address("112 Steppes Pl").station(3).build()
        );
        serviceUnderTest = new FirestationService(dataLoader);
    }

    @Test
    void testGetFirestation_found() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);
        List<Firestation> result = serviceUnderTest.getFirestations();
        assertEquals(firestationsMock, result);
    }

    @Test
    void testGetFirestation_noFound() {
        when(dataLoader.getFirestations()).thenReturn(Collections.emptyList());
        List<Firestation> result = serviceUnderTest.getFirestations();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFirestationByAdress_found() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        Optional<Firestation> result = serviceUnderTest.getFirestationByAdress("834 Binoc Ave");
        assertTrue(result.isPresent());
        assertEquals("834 Binoc Ave", result.get().getAddress());
        assertEquals(3, result.get().getStation());

        Optional<Firestation> result2 = serviceUnderTest.getFirestationByAdress("112 Steppes Pl");
        assertTrue(result2.isPresent());
        assertEquals("112 Steppes Pl", result2.get().getAddress());
        assertEquals(3, result2.get().getStation());
    }

    @Test
    void testGetFirestationByAdress_notFound() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);
        Optional<Firestation> result = serviceUnderTest.getFirestationByAdress("Non Existing Street");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAddressesByStationIDs_found() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        List<Integer> ids = Arrays.asList(1, 2, 3);
        List<String> result = serviceUnderTest.getAddressesByStationIDs(ids);

        assertEquals(4, result.size());
        assertTrue(result.contains("1509 Culver St"));
        assertTrue(result.contains("29 15th St"));
        assertTrue(result.contains("834 Binoc Ave"));
        assertTrue(result.contains("112 Steppes Pl"));
    }

    @Test
    void testGetAddressesByStationIDs_noneFound() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        List<Integer> ids = Collections.singletonList(99);
        List<String> result = serviceUnderTest.getAddressesByStationIDs(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFirestationsByID_found() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        List<Firestation> result = serviceUnderTest.getFirestationsByID(3);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(f -> f.getAddress().equals("834 Binoc Ave")));
        assertTrue(result.stream().anyMatch(f -> f.getAddress().equals("112 Steppes Pl")));
    }

    @Test
    void testGetFirestationsByID_noneFound() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        List<Firestation> result = serviceUnderTest.getFirestationsByID(99);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAddressesByStationID_found() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        List<String> result = serviceUnderTest.getAddressesByStationID(3);
        assertEquals(2, result.size());
        assertTrue(result.contains("834 Binoc Ave"));
        assertTrue(result.contains("112 Steppes Pl"));
    }

    @Test
    void testGetAddressesByStationID_noneFound() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        List<String> result = serviceUnderTest.getAddressesByStationID(99);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFirestationsByID_emptyData() {
        when(dataLoader.getFirestations()).thenReturn(Collections.emptyList());
        List<Firestation> result = serviceUnderTest.getFirestationsByID(3);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAddressesByStationID_emptyData() {
        when(dataLoader.getFirestations()).thenReturn(Collections.emptyList());
        List<String> result = serviceUnderTest.getAddressesByStationID(3);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAddressesByStationIDs_emptyData() {
        when(dataLoader.getFirestations()).thenReturn(Collections.emptyList());
        List<String> result = serviceUnderTest.getAddressesByStationIDs(Arrays.asList(1, 2, 3));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetFirestationNumberByAddress_found() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        Optional<Integer> result = serviceUnderTest.getFirestationNumberByAddress("834 Binoc Ave");
        assertTrue(result.isPresent());
        assertEquals(3, result.get());

        Optional<Integer> result2 = serviceUnderTest.getFirestationNumberByAddress("1509 Culver St");
        assertTrue(result2.isPresent());
        assertEquals(1, result2.get());
    }

    @Test
    void testGetFirestationNumberByAddress_notFound() {
        when(dataLoader.getFirestations()).thenReturn(firestationsMock);

        Optional<Integer> result = serviceUnderTest.getFirestationNumberByAddress("Non Existing Street");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFirestationNumberByAddress_emptyData() {
        when(dataLoader.getFirestations()).thenReturn(Collections.emptyList());

        Optional<Integer> result = serviceUnderTest.getFirestationNumberByAddress("834 Binoc Ave");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
