package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PersonService personService;

    @Autowired
    ObjectMapper objectMapper;

    // Constantes de test
    private final int EXISTING_STATION_ID = 2;
    private final int NOT_FOUND_STATION_ID = 8;
    private final String EXISTING_ADDRESS = "1509 Culver St";
    private final String NOT_FOUND_ADDRESS = "12 Perrault Street";
    private final String EXISTING_LASTNAME = "Smith";
    private final String NOT_FOUND_LASTNAME = "Nobody";
    private final String EXISTING_CITY = "Paris";
    private final String NOT_FOUND_CITY = "Nowhere";

    private Person person;
    private PersonsByFirestationIDReturn personsByFirestationIDReturn;
    private ChildAlertDTO childAlertDto;
    private PhoneAlertByFirestationDTO phoneAlertDto;
    private FireAddressReturnDTO fireAddressDto;
    private FireAddressResidentDTO residentDto;
    private PersonInfoByNameDTO personInfoByNameDto;

    @BeforeEach
    void setUp() {
        // Un HouseholdMemberDTO pour le ChildAlertDTO
        HouseholdMemberDTO householdMember = HouseholdMemberDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .build();

        // PersonByFirestationID pour PersonsByFirestationIDReturn
        PersonByFirestationID personByFirestationID = PersonByFirestationID.builder()
                .firstName("John")
                .lastName("Doe")
                .adress(EXISTING_ADDRESS)
                .build();

        // Personne pour le test d’ajout/màj
        person = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address(EXISTING_ADDRESS)

                .build();

        personsByFirestationIDReturn = PersonsByFirestationIDReturn.builder()
                .persons(List.of(personByFirestationID))
                .countOfAdults(3)
                .countOfChilds(1)
                .build();

        childAlertDto = ChildAlertDTO.builder()
                .firstName("Tom")
                .lastName("Doe")
                .age(5)
                .householdMembers(List.of(householdMember))
                .build();

        phoneAlertDto = PhoneAlertByFirestationDTO.builder()
                .phoneNumbers(List.of("0102030405"))
                .build();

        residentDto = FireAddressResidentDTO.builder()
                .firstName("Anna")
                .lastName("Smith")
                .phone("0601020304")
                .age(30)
                .medications(List.of("Doliprane"))
                .allergies(List.of("Pollen"))
                .build();

        fireAddressDto = FireAddressReturnDTO.builder()
                .stationNumber(EXISTING_STATION_ID)
                .residents(List.of(residentDto))
                .build();

        personInfoByNameDto = PersonInfoByNameDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .address(EXISTING_ADDRESS)
                .email("john.doe@email.com")
                .age(38)
                .medications(List.of("Doliprane"))
                .allergies(List.of("Pollen"))
                .build();
    }

    // FIRESTATION TESTS
    @Test
    public void testGetPersonsByFirestationId_found() throws Exception {
        when(personService.getAllPersonsByDependingOfFirestationID(EXISTING_STATION_ID))
                .thenReturn(Optional.of(personsByFirestationIDReturn));

        mockMvc.perform(get("/firestation?stationNumber={station}", EXISTING_STATION_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPersonsByFirestationId_noFound() throws Exception {
        when(personService.getAllPersonsByDependingOfFirestationID(NOT_FOUND_STATION_ID))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/firestation?stationNumber={station}", NOT_FOUND_STATION_ID))
                .andExpect(status().is(404));
    }

    // CHILD ALERT TESTS
    @Test
    public void testGetChildsByAddress_found() throws Exception {
        when(personService.getChildsByAdress(EXISTING_ADDRESS)).thenReturn(List.of(childAlertDto));

        mockMvc.perform(get("/childAlert?address={address}", EXISTING_ADDRESS))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetChildsByAddress_noFound() throws Exception {
        when(personService.getChildsByAdress(NOT_FOUND_ADDRESS)).thenReturn(List.of());

        mockMvc.perform(get("/childAlert?address={address}", NOT_FOUND_ADDRESS))
                .andExpect(status().is(404));
    }

    // PHONE ALERT
    @Test
    public void testGetPhonesByFirestationID_found() throws Exception {
        when(personService.getPhoneAlertByFirestation(EXISTING_STATION_ID)).thenReturn(Optional.of(phoneAlertDto));

        mockMvc.perform(get("/phoneAlert?firestation={id}", EXISTING_STATION_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPhonesByFirestationID_notFound() throws Exception {
        when(personService.getPhoneAlertByFirestation(NOT_FOUND_STATION_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/phoneAlert?firestation={id}", NOT_FOUND_STATION_ID))
                .andExpect(status().is(404));
    }

    // FIRE
    @Test
    public void testGetFireInfo_found() throws Exception {
        when(personService.getHouseholdInfoByAddress(EXISTING_ADDRESS)).thenReturn(Optional.of(fireAddressDto));
        mockMvc.perform(get("/fire?address={address}", EXISTING_ADDRESS))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFireInfo_notFound() throws Exception {
        when(personService.getHouseholdInfoByAddress(NOT_FOUND_ADDRESS)).thenReturn(Optional.empty());
        mockMvc.perform(get("/fire?address={address}", NOT_FOUND_ADDRESS))
                .andExpect(status().is(404));
    }

    // FLOOD/STATIONS
    @Test
    public void testGetFloodInfoByStations_found() throws Exception {
        Map<String, List<FireAddressResidentDTO>> floodMap = Map.of(EXISTING_ADDRESS, List.of(residentDto));
        when(personService.getFloodInfoByStations(List.of(EXISTING_STATION_ID))).thenReturn(floodMap);

        mockMvc.perform(get("/flood/stations?stations={ids}", EXISTING_STATION_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFloodInfoByStations_notFound() throws Exception {
        when(personService.getFloodInfoByStations(List.of(NOT_FOUND_STATION_ID))).thenReturn(Map.of());

        mockMvc.perform(get("/flood/stations?stations={ids}", NOT_FOUND_STATION_ID))
                .andExpect(status().is(404));
    }

    // PERSONINFO
    @Test
    public void testGetPersonInfoByName_found() throws Exception {
        when(personService.getPersonsInfoByLastName(EXISTING_LASTNAME)).thenReturn(List.of(personInfoByNameDto));
        mockMvc.perform(get("/personInfo?lastName={name}", EXISTING_LASTNAME))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPersonInfoByName_notFound() throws Exception {
        when(personService.getPersonsInfoByLastName(NOT_FOUND_LASTNAME)).thenReturn(List.of());
        mockMvc.perform(get("/personInfo?lastName={name}", NOT_FOUND_LASTNAME))
                .andExpect(status().is(404));
    }

    // COMMUNITY EMAIL
    @Test
    public void testGetCommunityEmail_found() throws Exception {
        when(personService.getEmailsByCity(EXISTING_CITY)).thenReturn(List.of("foo@email.com"));
        mockMvc.perform(get("/communityEmail?city={city}", EXISTING_CITY))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCommunityEmail_notFound() throws Exception {
        when(personService.getEmailsByCity(NOT_FOUND_CITY)).thenReturn(List.of());
        mockMvc.perform(get("/communityEmail?city={city}", NOT_FOUND_CITY))
                .andExpect(status().is(404));
    }

    // ADD PERSON
    @Test
    public void testAddPerson_created() throws Exception {
        when(personService.addPerson(any(Person.class))).thenReturn(Optional.of(person));
        String body = objectMapper.writeValueAsString(person);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(201));
    }

    @Test
    public void testAddPerson_conflict() throws Exception {
        when(personService.addPerson(any(Person.class))).thenReturn(Optional.empty());
        String body = objectMapper.writeValueAsString(person);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(409));
    }

    // UPDATE PERSON
    @Test
    public void testUpdatePerson_updated() throws Exception {
        when(personService.updatePerson(any(Person.class))).thenReturn(Optional.of(person));
        String body = objectMapper.writeValueAsString(person);

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePerson_notFound() throws Exception {
        when(personService.updatePerson(any(Person.class))).thenReturn(Optional.empty());
        String body = objectMapper.writeValueAsString(person);

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(410));
    }

    // DELETE PERSON
    @Test
    public void testDeletePerson_deleted() throws Exception {
        when(personService.deletePerson(anyString(), anyString())).thenReturn(true);


        mockMvc.perform(delete("/person?firstName=John&lastName=Doe")).andExpect(status().isOk());
    }

    @Test
    public void testDeletePerson_noDeleted() throws Exception {
        when(personService.deletePerson(anyString(), anyString())).thenReturn(false);


        mockMvc.perform(delete("/person?firstName=John&lastName=Doe")).andExpect(status().is(410));
    }


}
