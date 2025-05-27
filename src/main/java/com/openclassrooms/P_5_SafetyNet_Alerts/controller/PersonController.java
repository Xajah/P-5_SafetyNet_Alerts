package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    // -------------------- /firestation?stationNumber=xx --------------------- //
    @GetMapping("/firestation")
    public ResponseEntity<Optional<PersonsByFirestationIDReturn>> getPersonsByFirestationId(
            @RequestParam("stationNumber") int stationNumber) {
        Optional<PersonsByFirestationIDReturn> result = personService.getAllPersonsByDependingOfFirestationID(stationNumber);
        if (result.isEmpty())
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(result);
    }

    // -------------------- /childAlert?address=xxx --------------------- //
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildAlertDTO>> getChildsByAddress(@RequestParam("address") String address) {
        List<ChildAlertDTO> result = personService.getChildsByAdress(address);
        if (result.isEmpty())
            return ResponseEntity.status(404).body(result);
        return ResponseEntity.ok(result);
    }
    // -------------------- /phoneAlert?firestation=xx --------------------- //
    @GetMapping("/phoneAlert")
    public ResponseEntity<Optional<PhoneAlertByFirestationDTO>> getPhonesByFirestationID(@RequestParam int firestation){
        Optional<PhoneAlertByFirestationDTO> result = personService.getPhoneAlertByFirestation(firestation);
        if(result.isEmpty()){return ResponseEntity.status(404).body(result);}
        return ResponseEntity.ok(result);
    }
    // -------------------- /fire?address=xxx --------------------- //
    @GetMapping("/fire")
    public ResponseEntity<Optional<FireAddressReturnDTO>> getStationAndPeopleForAFire(@RequestParam String address){
        Optional<FireAddressReturnDTO> result = personService.getHouseholdInfoByAddress(address);
        if (result.isEmpty()){
            return ResponseEntity.status(404).build();}
        return ResponseEntity.ok(result);
        }
    // -------------------- /flood/stations?stations=xx,yy,zz --------------------- //
    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<FireAddressResidentDTO>>> getFloodInfoByStations(
            @RequestParam("stations") List<Integer> stationNumbers) {
        Map<String, List<FireAddressResidentDTO>> result = personService.getFloodInfoByStations(stationNumbers);
        if (result.isEmpty()) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.ok(result);
    }

    // -------------------- /personInfo?lastName=xxx --------------------- //
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoByNameDTO>> getPersonInfoByName(@RequestParam("lastName") String lastName) {
        List<PersonInfoByNameDTO> result = personService.getPersonsInfoByLastName(lastName);
        if (result.isEmpty())
            return ResponseEntity.status(404).body(result);
        return ResponseEntity.ok(result);
    }

    // -------------------- /communityEmail?city=xxx --------------------- //
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getEmailsByCity(@RequestParam("city") String city) {
        List<String> emails = personService.getEmailsByCity(city);
        if (emails.isEmpty())
            return ResponseEntity.status(404).body(emails);
        return ResponseEntity.ok(emails);

        //-----------EndPoint----------//
    }
    @PostMapping("/person")
    public  ResponseEntity<Optional<Person>> addPerson(@RequestBody Person person){
        Optional<Person> result = personService.addPerson(person);
        if(result.isPresent()){return ResponseEntity.status(201).body(result);
    }return ResponseEntity.status(409).build();}

    @PutMapping("/person")
    public ResponseEntity<Optional<Person>> updatePerson(@RequestBody Person person){
        Optional<Person> result = personService.updatePerson(person);
        if (result.isPresent()){return ResponseEntity.status(200).body(result);}
        return ResponseEntity.status(410).build();

    }
    @DeleteMapping("/person")
    public  ResponseEntity<Void> deletePerson (@RequestParam String firstName,@RequestParam String lastName){
        boolean delete = personService.deletePerson(firstName, lastName);
        if(delete){ return ResponseEntity.status(200).build();}
        return ResponseEntity.status(410).build();
    }
}