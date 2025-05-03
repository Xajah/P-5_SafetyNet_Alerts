package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final DataLoader dataLoader;

    public List<MedicalRecord> getMedicalRecords() {
        return dataLoader.getMedicalRecords();
    }

    public MedicalRecord getMedicalRecordByName(String firstName, String lastName) {
        // La recherche "fonctionnelle", renvoie le premier trouvé ou null
        return getMedicalRecords().stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);
    }
}
