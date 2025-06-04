package com.openclassrooms.P_5_SafetyNet_Alerts.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Composant Spring chargé de l'importation initiale des données de l'application
 * depuis un fichier data.json situé sur le disque (modifiable).
 * Implemente {@link CommandLineRunner} pour être exécuté au lancement de l'application.
 */
@Component
@RequiredArgsConstructor
@Data
public class DataLoader implements CommandLineRunner {

    // Chemin du fichier de données utilisé à la fois en lecture et en écriture
    private static final String DATA_FILE_PATH = "Data/data.json";

    private List<Person> persons;
    private List<Firestation> firestations;
    private List<MedicalRecord> medicalRecords;

    /**
     * Méthode exécutée au lancement de l'application.
     * Cette méthode charge les données de l'application depuis le fichier "Data/data.json"
     * sur le disque, et les map sous forme de listes d'objets métier.
     */
    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        File file = new File(DATA_FILE_PATH);
        // charge les données depuis le fichier du disque
        DataWrapper data = mapper.readValue(file, DataWrapper.class);
        this.persons = data.getPersons();
        this.firestations = data.getFirestations();
        this.medicalRecords = data.getMedicalrecords();

        System.out.println("Données chargées depuis " + DATA_FILE_PATH + " !");
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<Firestation> getFirestations() {
        return firestations;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    /**
     * Ecrit l'état courant des listes dans le fichier de données
     */
    public void saveData() {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            DataWrapper data = new DataWrapper();
            data.setPersons(this.persons);
            data.setFirestations(this.firestations);
            data.setMedicalrecords(this.medicalRecords);

            File file = new File(DATA_FILE_PATH);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);

            System.out.println("Données sauvegardées dans " + DATA_FILE_PATH + " !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}
