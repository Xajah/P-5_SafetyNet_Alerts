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
import java.util.List;

/**
 * Composant Spring chargé de l'importation initiale des données de l'application
 * depuis un fichier data.json situé dans le classpath.
 * Implemente {@link CommandLineRunner} pour être exécuté au lancement de l'application.
 */
@Component
@RequiredArgsConstructor
@Data
public class DataLoader implements CommandLineRunner {

    /**
     * Liste des personnes chargées depuis le fichier JSON de données.
     */
    private List<Person> persons;

    /**
     * Liste des associations adresse-casernes chargées depuis le fichier JSON de données.
     */
    private List<Firestation> firestations;

    /**
     * Liste des dossiers médicaux chargés depuis le fichier JSON de données.
     */
    private List<MedicalRecord> medicalRecords;

    /**
     * Méthode exécutée au lancement de l'application.
     * Cette méthode charge les données de l'application depuis le fichier "data.json"
     * et les map sous forme de listes d'objets métier.
     *
     * @param args arguments de ligne de commande (non utilisés ici)
     * @throws Exception en cas d'échec de lecture ou de parsing du fichier JSON
     */
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

    /**
     * @return La liste des personnes chargées.
     */
    public List<Person> getPersons() { return persons; }

    /**
     * @return La liste des casernes-adresses chargées.
     */
    public List<Firestation> getFirestations() { return firestations; }

    /**
     * @return La liste des dossiers médicaux chargés.
     */
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
}
