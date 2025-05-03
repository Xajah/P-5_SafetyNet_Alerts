package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    // -------------------- /firestation?stationNumber=xx --------------------- //
    @GetMapping("/firestation")
    public ResponseEntity<PersonsByFirestationIDReturn> getPersonsByFirestationId(
            @RequestParam("stationNumber") int stationNumber) {
        PersonsByFirestationIDReturn result = personService.getAllPersonsByDependingOfFirestationID(stationNumber);
        if (result == null)
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(result);
    }

    // -------------------- /childAlert?address=xxx --------------------- //
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildAlertDTO>> getChildsByAddress(@RequestParam("address") String address) {
        List<ChildAlertDTO> result = personService.getChildsByAdress(address);
        if (result.isEmpty())
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(result);
    }
    // -------------------- /phoneAlert?firestation=xx --------------------- //
    @GetMapping("/phoneAlert")
    public ResponseEntity<PhoneAlertByFirestationDTO> getPhonesByFirestationID(@RequestParam int id){
        PhoneAlertByFirestationDTO result = personService.getPhoneAlertByFirestation(id);
        if(result == null){return ResponseEntity.status(404).build();}
        return ResponseEntity.ok(result);
    }
    // -------------------- /fire?address=xxx --------------------- //
    @GetMapping("/fire")
    public ResponseEntity<FireAddressReturnDTO> getStationAndPeopleForAFire(@RequestParam String adress){
        FireAddressReturnDTO result = personService.getHouseholdInfoByAddress(adress);
        if (result == null){
            return ResponseEntity.status(404).build();}
        return ResponseEntity.ok(result);
        }
    // -------------------- /flood/stations?stations=xx,yy,zz --------------------- //
    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<FireAddressResidentDTO>>> getFloodInfoByStations(
            @RequestParam("stations") List<Integer> stationNumbers) {
        Map<String, List<FireAddressResidentDTO>> result = personService.getFloodInfoByStations(stationNumbers);
        if (result.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(result);
    }

    // -------------------- /personInfo?lastName=xxx --------------------- //
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoByNameDTO>> getPersonInfoByName(@RequestParam("lastName") String lastName) {
        List<PersonInfoByNameDTO> result = personService.getPersonsInfoByLastName(lastName);
        if (result.isEmpty())
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(result);
    }

    // -------------------- /communityEmail?city=xxx --------------------- //
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getEmailsByCity(@RequestParam("city") String city) {
        List<String> emails = personService.getEmailsByCity(city);
        if (emails.isEmpty())
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(emails);
    }
}