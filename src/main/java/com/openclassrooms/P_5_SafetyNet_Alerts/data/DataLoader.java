package com.openclassrooms.P_5_SafetyNet_Alerts.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.DataWrapper;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.PersonService;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.FirestationService;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PersonService personService;
    private final FirestationService firestationService;
    private final MedicalRecordService medicalRecordService;



    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("data.json").getInputStream();

        DataWrapper data = mapper.readValue(is, DataWrapper.class);

        // Injection des données dans chaque service
        personService.setPersons(data.getPersons());
        firestationService.setFirestations(data.getFirestations());
        medicalRecordService.setMedicalRecords(data.getMedicalRecords());

        System.out.println("Données chargées depuis data.json !");
    }
}
