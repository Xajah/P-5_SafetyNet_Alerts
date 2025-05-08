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
        // return Optional<MedicalRecord> : soit présent soit Optional.empty()
    }
}
