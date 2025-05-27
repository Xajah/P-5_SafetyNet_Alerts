package com.openclassrooms.P_5_SafetyNet_Alerts.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
@Data
public class DataLoader implements CommandLineRunner {

    private List<Person> persons;
    private List<Firestation> firestations;
    private List<MedicalRecord> medicalRecords;

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        InputStream is = new ClassPathResource("Data/data.json").getInputStream();

        DataWrapper data = mapper.readValue(is, DataWrapper.class);
        this.persons = data.getPersons();
        this.firestations = data.getFirestations();
        this.medicalRecords = data.getMedicalrecords();

        System.out.println("Données chargées depuis data.json !");
    }

    public List<Person> getPersons() { return persons; }
    public List<Firestation> getFirestations() { return firestations; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
}