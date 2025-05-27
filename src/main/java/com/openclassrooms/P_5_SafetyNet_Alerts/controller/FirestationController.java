package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.FirestationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequiredArgsConstructor
public class FirestationController {

    private final FirestationService firestationService;

    //post
    @PostMapping("/firestation")
    public ResponseEntity<Optional<Firestation>> addFirestation(@RequestBody Firestation firestation){
        Optional<Firestation> result = firestationService.addFirestation(firestation);
        if(result.isPresent()){return ResponseEntity.status(201).body(result);}
        return ResponseEntity.status(409).build();
    }
    @PutMapping("/firestation")
    public  ResponseEntity<Optional<Firestation>> updateFirestation (@RequestBody Firestation firestation){
        Optional<Firestation> result = firestationService.updateFirestation(firestation);
        if (result.isPresent()){return ResponseEntity.status(200).body(result);}
        return ResponseEntity.status(410).build();
    }

    @DeleteMapping (path="/firestation",params = "firestation")
    public ResponseEntity<Void> deleteFirestationWithID(@RequestParam int firestation){
        boolean deleted = firestationService.deleteFirestationMappingById(firestation);
        if(deleted){return ResponseEntity.status(200).build();}
        else {return ResponseEntity.status(410).build();}

    }
    @DeleteMapping (path="/firestation",params = "address")
    public ResponseEntity<Void> deleteFirestationWithAdresse(@RequestParam String address){
        boolean deleted = firestationService.deleteFirestationMappingByAdress(address);
        if(deleted){return ResponseEntity.status(200).build();}
        else {return ResponseEntity.status(410).build();}

    }
}
