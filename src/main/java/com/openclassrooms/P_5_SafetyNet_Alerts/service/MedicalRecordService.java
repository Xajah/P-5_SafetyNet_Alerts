package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final DataLoader dataLoader;

    public List<MedicalRecord> getMedicalRecords() {
        return dataLoader.getMedicalRecords();
    }

    public Optional<MedicalRecord> getMedicalRecordByName(String firstName, String lastName) {
        return getMedicalRecords().stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .findFirst();

    }
    //-----------------------------------------EndPoints-----------------------------------------//

    //POST - Creation d'un nouveau registre medicale
    public Optional<MedicalRecord> addMedicalRecord(MedicalRecord medicalRecord){
        if(medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null){return Optional.empty();}
        List<MedicalRecord> medicalRecords =dataLoader.getMedicalRecords();
        if (medicalRecords.isEmpty()){return Optional.empty();}

        Boolean exist = medicalRecords.stream()
                .anyMatch(m -> m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                        && m.getLastName().equalsIgnoreCase(medicalRecord.getLastName()));
        if(exist){
            return Optional.empty();}
        medicalRecords.add(medicalRecord);
        return Optional.of(medicalRecord);
    }
    //PUT - Modification d'un registre medical existant
    public Optional<MedicalRecord> updateMedicalRecord(MedicalRecord medicalRecord){
        if(medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null){return Optional.empty();}
        List<MedicalRecord> medicalRecords = dataLoader.getMedicalRecords();
        if (medicalRecords.isEmpty()){return Optional.empty();}

        Optional<MedicalRecord> resultOpt = medicalRecords.stream().filter(m ->
                m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName()) && m.getLastName().equalsIgnoreCase(medicalRecord.getLastName())).findFirst();

        resultOpt.ifPresent(r -> {
                r.setMedications(medicalRecord.getMedications());
                r.setBirthdate(medicalRecord.getBirthdate());
                r.setAllergies(medicalRecord.getAllergies());  } );
        return resultOpt;
    }
    //DELETE : Supprimer un registre medical
    public  Boolean deleteMedicalRecord(String firstName, String lastName){
        if(firstName == null || lastName == null){return false;}
        List<MedicalRecord> medicalRecords = dataLoader.getMedicalRecords();
        if (medicalRecords.isEmpty()){return false;}

        return medicalRecords.removeIf(m ->
                m.getLastName().equalsIgnoreCase(lastName) && m.getFirstName().equalsIgnoreCase(firstName));
    }
}
