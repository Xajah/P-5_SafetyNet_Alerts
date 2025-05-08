package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    DataLoader dataLoader;
    @Mock
    MedicalRecordService medicalRecordService;
    @Mock
    FirestationService firestationService;

    PersonService serviceUnderTest;

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    List<Person> personsMock;
    List<MedicalRecord> medicalRecordsMock;

    @BeforeEach
    void setUp() {
        personsMock = Arrays.asList(
                Person.builder().firstName("John").lastName("Boyd").address("1509 Culver St").city("Culver").phone("111-1111").email("john@domain.com").build(),
                Person.builder().firstName("Jacob").lastName("Boyd").address("1509 Culver St").city("Culver").phone("222-2222").email("jacob@domain.com").build(),
                Person.builder().firstName("Tenley").lastName("Boyd").address("1509 Culver St").city("Culver").phone("333-3333").email("tenley@domain.com").build(),
                Person.builder().firstName("Peter").lastName("Duncan").address("29 15th St").city("Springfield").phone("444-4444").email("peter@domain.com").build()
        );
        medicalRecordsMock = Arrays.asList(
                MedicalRecord.builder().firstName("John").lastName("Boyd").birthdate(LocalDate.parse("03/06/1984", formatter)).medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg")).allergies(Arrays.asList("nillacilan")).build(),
                MedicalRecord.builder().firstName("Jacob").lastName("Boyd").birthdate(LocalDate.parse("03/06/1989", formatter)).medications(Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg")).allergies(Arrays.asList()).build(),
                MedicalRecord.builder().firstName("Tenley").lastName("Boyd").birthdate(LocalDate.parse("02/18/2012", formatter)).medications(Arrays.asList()).allergies(Arrays.asList("peanut")).build(),
                MedicalRecord.builder().firstName("Peter").lastName("Duncan").birthdate(LocalDate.parse("09/06/2000", formatter)).medications(Arrays.asList("dodoxadin:30mg")).allergies(Arrays.asList("shellfish")).build()
        );
        serviceUnderTest = new PersonService(dataLoader, medicalRecordService, firestationService);
    }

    @Test
    void testGetAllPersonsByDependingOfFirestationID_basic() {
        when(firestationService.getAddressesByStationID(1)).thenReturn(Collections.singletonList("1509 Culver St"));
        when(dataLoader.getPersons()).thenReturn(personsMock);

        personsMock.stream()
                .filter(p -> "1509 Culver St".equals(p.getAddress()))
                .forEach(p -> {
                    Optional<MedicalRecord> mrOpt = medicalRecordsMock.stream()
                            .filter(m -> m.getFirstName().equals(p.getFirstName()) && m.getLastName().equals(p.getLastName()))
                            .findFirst();
                    when(medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName()))
                            .thenReturn(mrOpt);
                });

        Optional<PersonsByFirestationIDReturn> result = serviceUnderTest.getAllPersonsByDependingOfFirestationID(1);
        assertNotNull(result);
        assertFalse(result.get().getPersons().isEmpty());
    }

    @Test
    void testGetAllPersonsByDependingOfFirestationID_noPersons() {
        when(firestationService.getAddressesByStationID(99)).thenReturn(Collections.singletonList("NoSuchAddress"));
        when(dataLoader.getPersons()).thenReturn(personsMock);

        Optional<PersonsByFirestationIDReturn> result = serviceUnderTest.getAllPersonsByDependingOfFirestationID(99);
        assertNotNull(result);
        assertTrue(result.get().getPersons().isEmpty());
        assertEquals(0, result.get().getCountOfAdults());
        assertEquals(0, result.get().getCountOfChilds());
    }

    @Test
    void testGetAllPersonsByDependingOfFirestationID_noMedicalRecord() {
        when(firestationService.getAddressesByStationID(1)).thenReturn(Collections.singletonList("1509 Culver St"));
        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().firstName("NoMR").lastName("NoMR").address("1509 Culver St").build()
        ));
        when(medicalRecordService.getMedicalRecordByName(anyString(), anyString())).thenReturn(Optional.empty());

        Optional<PersonsByFirestationIDReturn> result = serviceUnderTest.getAllPersonsByDependingOfFirestationID(1);
        assertNotNull(result);
        assertEquals(1, result.get().getPersons().size());
        // Le calcul d'âge tombera sur 0
        assertEquals(0, result.get().getCountOfAdults());
        assertEquals(1, result.get().getCountOfChilds());
    }

    @Test
    void testGetHouseholdInfoByAddress_basic() {
        when(firestationService.getFirestationNumberByAddress("1509 Culver St")).thenReturn(Optional.of(1));
        when(dataLoader.getPersons()).thenReturn(personsMock);

        personsMock.stream()
                .filter(p -> "1509 Culver St".equals(p.getAddress()))
                .forEach(p -> {
                    Optional<MedicalRecord> mrOpt = medicalRecordsMock.stream()
                            .filter(m -> m.getFirstName().equals(p.getFirstName()) && m.getLastName().equals(p.getLastName()))
                            .findFirst();
                    when(medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName()))
                            .thenReturn(mrOpt);
                });

        Optional<FireAddressReturnDTO> result = serviceUnderTest.getHouseholdInfoByAddress("1509 Culver St");
        assertNotNull(result);
        assertTrue(result.isPresent());
        FireAddressReturnDTO dto = result.get();
        assertEquals(1, dto.getStationNumber());
        assertFalse(dto.getResidents().isEmpty());

    }

    @Test
    void testGetHouseholdInfoByAddress_noMatch() {
        when(firestationService.getFirestationNumberByAddress("unknown_address")).thenReturn(Optional.empty());
        when(dataLoader.getPersons()).thenReturn(personsMock);

        Optional<FireAddressReturnDTO> result = serviceUnderTest.getHouseholdInfoByAddress("unknown_address");
        assertNotNull(result);
        assertTrue(result.isPresent());
        FireAddressReturnDTO dto = result.get();
        assertEquals(0, dto.getStationNumber());
        assertTrue(dto.getResidents().isEmpty()); // personne n'habite là !
    }


    @Test
    void testGetHouseholdInfoByAddress_noMedicalRecord() {

        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().firstName("Foo").lastName("Bar").address("1509 Culver St").build()
        ));

        when(medicalRecordService.getMedicalRecordByName(anyString(), anyString())).thenReturn(Optional.empty());

        Optional<FireAddressReturnDTO> result = serviceUnderTest.getHouseholdInfoByAddress("1509 Culver St");
        assertNotNull(result);
        assertEquals(1, result.get().getResidents().size());
        assertEquals(0, result.get().getResidents().get(0).getAge());
        assertTrue(result.get().getResidents().get(0).getMedications().isEmpty());
        assertTrue(result.get().getResidents().get(0).getAllergies().isEmpty());
    }

    @Test
    void testGetFloodInfoByStations_basic() {
        when(firestationService.getAddressesByStationIDs(Arrays.asList(1, 2))).thenReturn(Arrays.asList("1509 Culver St", "29 15th St"));
        when(dataLoader.getPersons()).thenReturn(personsMock);

        personsMock.forEach(p -> {
            Optional<MedicalRecord> mrOpt = medicalRecordsMock.stream()
                    .filter(m -> m.getFirstName().equals(p.getFirstName()) && m.getLastName().equals(p.getLastName()))
                    .findFirst();
            when(medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName()))
                    .thenReturn(mrOpt);
        });

        Map<String, List<FireAddressResidentDTO>> result = serviceUnderTest.getFloodInfoByStations(Arrays.asList(1, 2));
        assertNotNull(result);
        assertTrue(result.containsKey("1509 Culver St"));
        assertTrue(result.containsKey("29 15th St"));
        assertFalse(result.get("1509 Culver St").isEmpty());
        assertFalse(result.get("29 15th St").isEmpty());
    }

    @Test
    void testGetFloodInfoByStations_someAddressesEmpty() {
        when(firestationService.getAddressesByStationIDs(Arrays.asList(1,2)))
                .thenReturn(Arrays.asList("1509 Culver St", "EmptyAddr"));
        when(dataLoader.getPersons()).thenReturn(personsMock);
        Map<String, List<FireAddressResidentDTO>> result = serviceUnderTest.getFloodInfoByStations(Arrays.asList(1,2));
        assertNotNull(result);
        assertTrue(result.containsKey("EmptyAddr"));
        assertTrue(result.get("EmptyAddr").isEmpty());
    }

    @Test
    void testGetPersonsInfoByLastName_found() {
        when(dataLoader.getPersons()).thenReturn(personsMock);
        personsMock.forEach(p -> {
            Optional<MedicalRecord> mrOpt = medicalRecordsMock.stream()
                    .filter(m -> m.getFirstName().equals(p.getFirstName()) && m.getLastName().equals(p.getLastName()))
                    .findFirst();

        });

        List<PersonInfoByNameDTO> result = serviceUnderTest.getPersonsInfoByLastName("Boyd");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        for(var r : result) assertEquals("Boyd", r.getLastName());
    }

    @Test
    void testGetPersonsInfoByLastName_notFound() {
        when(dataLoader.getPersons()).thenReturn(personsMock);
        List<PersonInfoByNameDTO> result = serviceUnderTest.getPersonsInfoByLastName("NoName");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetEmailsByCity_found() {
        when(dataLoader.getPersons()).thenReturn(personsMock);
        List<String> emails = serviceUnderTest.getEmailsByCity("Culver");
        assertNotNull(emails);
        assertFalse(emails.isEmpty());
        assertEquals(3, emails.size());
    }

    @Test
    void testGetEmailsByCity_notFound() {
        when(dataLoader.getPersons()).thenReturn(personsMock);
        List<String> emails = serviceUnderTest.getEmailsByCity("SomeUnknownCity");
        assertNotNull(emails);
        assertTrue(emails.isEmpty());
    }
    @Test
    void testGetChildsByAddress_noResidents() {
        when(dataLoader.getPersons()).thenReturn(Collections.emptyList());
        List<ChildAlertDTO> result = serviceUnderTest.getChildsByAdress("NoSuchAddress");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetChildsByAddress_allAdults() {
        // Peter Duncan est adulte selon la date donnée dans mock
        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().firstName("Peter").lastName("Duncan").address("29 15th St").build()
        ));
        when(medicalRecordService.getMedicalRecordByName(any(), any()))
                .thenReturn(Optional.of(MedicalRecord.builder().firstName("Peter").lastName("Duncan")
                        .birthdate(LocalDate.now().minusYears(30)).build())); // 30 ans

        List<ChildAlertDTO> result = serviceUnderTest.getChildsByAdress("29 15th St");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetChildsByAddress_childWithNoMedicalRecord() {
        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().firstName("Kid").lastName("Test").address("66 Kids St").build()
        ));
        when(medicalRecordService.getMedicalRecordByName(any(), any())).thenReturn(Optional.empty());

        List<ChildAlertDTO> result = serviceUnderTest.getChildsByAdress("66 Kids St");
        assertNotNull(result);
        // Age == 0 donc considéré comme enfant
        assertEquals(1, result.size());
        assertEquals("Kid", result.get(0).getFirstName());
        assertEquals(0, result.get(0).getAge());
    }
    @Test
    void testGetPhoneAlertByFirestation_noAddresses() {
        when(firestationService.getAddressesByStationID(42)).thenReturn(Collections.emptyList());
        when(dataLoader.getPersons()).thenReturn(personsMock);

        Optional<PhoneAlertByFirestationDTO> result = serviceUnderTest.getPhoneAlertByFirestation(42);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getPhoneNumbers().isEmpty());
    }

    @Test
    void testGetPhoneAlertByFirestation_nullPhoneValues() {
        when(firestationService.getAddressesByStationID(1)).thenReturn(Collections.singletonList("1509 Culver St"));
        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().firstName("X").lastName("Y").address("1509 Culver St").phone(null).build()
        ));

        Optional<PhoneAlertByFirestationDTO> result = serviceUnderTest.getPhoneAlertByFirestation(1);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertTrue(result.get().getPhoneNumbers().isEmpty());
    }
    @Test
    void testGetFloodInfoByStations_noAddresses() {
        when(firestationService.getAddressesByStationIDs(Arrays.asList(99))).thenReturn(Collections.emptyList());
        Map<String, List<FireAddressResidentDTO>> result = serviceUnderTest.getFloodInfoByStations(Arrays.asList(99));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFloodInfoByStations_addressButNoPerson() {
        when(firestationService.getAddressesByStationIDs(Arrays.asList(1))).thenReturn(Collections.singletonList("EmptyHouse"));
        when(dataLoader.getPersons()).thenReturn(Collections.emptyList());

        Map<String, List<FireAddressResidentDTO>> result = serviceUnderTest.getFloodInfoByStations(Arrays.asList(1));
        assertNotNull(result);
        assertTrue(result.containsKey("EmptyHouse"));
        assertTrue(result.get("EmptyHouse").isEmpty());
    }

    @Test
    void testGetFloodInfoByStations_personNoMedicalRecord() {
        when(firestationService.getAddressesByStationIDs(Arrays.asList(1))).thenReturn(Collections.singletonList("1509 Culver St"));
        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().firstName("Nommr").lastName("Nommr").address("1509 Culver St").phone("07-07").build()
        ));
        when(medicalRecordService.getMedicalRecordByName(any(), any())).thenReturn(Optional.empty());
        Map<String, List<FireAddressResidentDTO>> result = serviceUnderTest.getFloodInfoByStations(Arrays.asList(1));
        assertNotNull(result);
        assertEquals(1, result.get("1509 Culver St").size());
        assertEquals(0, result.get("1509 Culver St").get(0).getAge());
    }
    @Test
    void testGetEmailsByCity_duplicates() {
        when(dataLoader.getPersons()).thenReturn(Arrays.asList(
                Person.builder().city("Metropolis").email("email@email.com").build(),
                Person.builder().city("Metropolis").email("email@email.com").build()
        ));
        List<String> emails = serviceUnderTest.getEmailsByCity("Metropolis");
        assertEquals(1, emails.size());
    }


}
