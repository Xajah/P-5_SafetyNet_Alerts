package com.openclassrooms.P_5_SafetyNet_Alerts.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TestPersonController_IT {

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

    // /firestation?stationNumber=1
    @Test
    void testGetPersonsByFirestationId_found() throws Exception {
        // Data: station 1 : 947 E. Rose Dr (3 pers), 644 Gershwin Cir, 908 73rd St
        // Enfants : il n'y a qu'un enfant Kendrik Stelzer né en 2014
        // Adultes : Brian, Shawna, Peter, Jamie, Reginold (5 adultes)
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOfAdults").value(5))
                .andExpect(jsonPath("$.countOfChilds").value(1));
    }

    @Test
    void testGetPersonsByFirestationId_notFound() throws Exception {
        mockMvc.perform(get("/firestation?stationNumber=99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // /childAlert?address=1509 Culver St
    @Test
    void testGetChildsByAddress_found() throws Exception {
        // A cette adresse, selon data, enfants: Tenley (2012) et Roger (2017)
        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // tableau de 2 enfants
                .andExpect(jsonPath("$[0].firstName", anyOf(is("Tenley"), is("Roger"))))
                .andExpect(jsonPath("$[1].firstName", anyOf(is("Tenley"), is("Roger"))));
    }

    @Test
    void testGetChildsByAddress_notFound() throws Exception {
        mockMvc.perform(get("/childAlert?address=Unknown Address"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // /phoneAlert?firestation=3
    @Test
    void testGetPhonesByFirestationID_found() throws Exception {
        // Firestation 3 : 1509 Culver St, 834 Binoc Ave, 748 Townings Dr, 112 Steppes Pl
        // Vérif présence de numéro '841-874-6512' (John Boyd )
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumbers", hasItem("841-874-6512")));
    }

    @Test
    void testGetPhonesByFirestationID_notFound() throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation=99"))
                .andExpect(status().isNotFound());
    }

    // /fire?address=834 Binoc Ave
    @Test
    void testGetStationAndPeopleForAFire_found() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "834 Binoc Ave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationNumber").value("3"))
                .andExpect(jsonPath("$.residents[0].firstName").value("Tessa"));
    }

    @Test
    void testGetStationAndPeopleForAFire_notFound() throws Exception {
        mockMvc.perform(get("/fire?address=Unknown"))
                .andExpect(status().isNotFound());
        //peut etre traiter comme une liste vide -> pour une liste vide pas 404, 200 avec vide -> a verifier ce cas et la gestion
    }

    // /flood/stations?stations=1,3
    @Test
    void testGetFloodInfoByStations_found() throws Exception {
        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['1509 Culver St']").exists()); // Couvre bien une adresse de la station 3
    }

    @Test
    void testGetFloodInfoByStations_notFound() throws Exception {
        mockMvc.perform(get("/flood/stations?stations=99"))
                .andExpect(status().isNotFound());
    }

    // /personInfo?lastName=Boyd
    @Test
    void testGetPersonInfoByName_found() throws Exception {
        // 6 Boyd (John, Jacob, Tenley, Roger, Felicia, Allison) dans la data
        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[*].firstName", hasItem("John")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Jacob")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Tenley")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Roger")));
    }

    @Test
    void testGetPersonInfoByName_notFound() throws Exception {
        mockMvc.perform(get("/personInfo?lastName=Unknown"))
                .andExpect(status().isNotFound());
    }

    // /communityEmail?city=Culver
    @Test
    void testGetEmailsByCity_found() throws Exception {
        // Beaucoup d'emails en ville Culver
        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("jaboyd@email.com")))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    void testGetEmailsByCity_notFound() throws Exception {
        mockMvc.perform(get("/communityEmail?city=Nowhere"))
                .andExpect(status().isNotFound());
    }

    // TESTS CRUD Person

    @Test
    void testAddPerson_ok() throws Exception {
        Person p = Person.builder()
                .firstName("TestFirst")
                .lastName("TestLast")
                .address("1500 Test Rd")
                .city("Culver")
                .zip("99999")
                .phone("999-888-7777")
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("TestFirst"))
                .andExpect(jsonPath("$.lastName").value("TestLast"));
    }

    @Test
    void testAddPerson_conflict() throws Exception {
        // John Boyd already exists in dataset
        Person p = Person.builder()
                .firstName("John")
                .lastName("Boyd")
                .address("1509 Culver St")
                .city("Culver")
                .zip("97451")
                .phone("841-874-6512")
                .email("jaboyd@email.com")
                .build();

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdatePerson_found() throws Exception {
        // Changer le numéro de Felicia Boyd
        Person p = Person.builder()
                .firstName("Felicia")
                .lastName("Boyd")
                .address("1509 Culver St")
                .city("Culver")
                .zip("97451")
                .phone("000-000-0000")
                .email("jaboyd@email.com")
                .build();

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Felicia"))
                .andExpect(jsonPath("$.phone").value("000-000-0000"));
    }

    @Test
    void testUpdatePerson_notFound() throws Exception {
        Person p = Person.builder()
                .firstName("DoesNot")
                .lastName("Exist")
                .address("Nowhere")
                .city("NoCity")
                .zip("00000")
                .phone("000-000-0000")
                .email("no@email.com")
                .build();

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isGone());
    }

    @Test
    void testDeletePerson_found() throws Exception {
        mockMvc.perform(delete("/person")
                        .param("firstName", "Tony")
                        .param("lastName", "Cooper"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePerson_notFound() throws Exception {
        mockMvc.perform(delete("/person")
                        .param("firstName", "Nobody")
                        .param("lastName", "Here"))
                .andExpect(status().isGone());
    }

}
