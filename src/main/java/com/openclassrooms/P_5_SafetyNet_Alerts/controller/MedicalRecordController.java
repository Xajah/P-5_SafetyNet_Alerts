package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;


    @PostMapping("/medicalRecord")
    public ResponseEntity<Optional<MedicalRecord>> addMedicalRecord(@RequestBody MedicalRecord medicalRecord){
        Optional<MedicalRecord> result = medicalRecordService.addMedicalRecord(medicalRecord);
        if (result.isPresent()){return ResponseEntity.status(201).body(result);}
        return ResponseEntity.status(409).build();
    }
    @PutMapping("/medicalRecord")
    public ResponseEntity<Optional<MedicalRecord>> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord){
        Optional<MedicalRecord> result = medicalRecordService.updateMedicalRecord(medicalRecord);
        if (result.isPresent()){return ResponseEntity.status(200).body(result);}
        return  ResponseEntity.status(410).build();
    }
    @DeleteMapping("/medicalRecord")
    public ResponseEntity<Void> deleteMedicalRecord (@RequestParam String firstName, @RequestParam String lastName){
        Boolean delete = medicalRecordService.deleteMedicalRecord(firstName,lastName);
        if (delete){return ResponseEntity.status(200).build();}
            return ResponseEntity.status(410).build();

    }

}
