package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service métier dédié à la gestion des dossiers médicaux ({@link MedicalRecord}).
 * <p>
 * Permet de répondre aux besoins de l'application en matière de recherche, création,
 * modification et suppression de dossiers médicaux, ainsi que de fournir des informations
 * utiles aux contrôleurs REST (par exemple : dossier médical par personne, ajout ou suppression
 * de dossiers, etc.).
 */

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final DataLoader dataLoader;

    /**
     * Récupère tous les dossiers médicaux.
     *
     * @return liste de MedicalRecord
     */
    public List<MedicalRecord> getMedicalRecords() {
        return dataLoader.getMedicalRecords();
    }

    /**
     * Retourne l'enregistrement médical d'une personne par prénom et nom.
     *
     * @param firstName prénom
     * @param lastName  nom
     * @return Optional contenant le dossier médical trouvé, ou vide si absent
     */
    public Optional<MedicalRecord> getMedicalRecordByName(String firstName, String lastName) {
        return getMedicalRecords().stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .findFirst();

    }
    //-----------------------------------------EndPoints-----------------------------------------//

    /**
     * Ajoute un dossier médical s'il n'existe pas déjà (prénom+nom).
     * Endpoint: POST /medicalRecord
     *
     * @param medicalRecord instance à ajouter
     * @return Optional du MedicalRecord ajouté, vide si déjà présent ou invalide
     */
    //POST - Creation d'un nouveau registre medicale
    public Optional<MedicalRecord> addMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null) {
            return Optional.empty();
        }
        List<MedicalRecord> medicalRecords = dataLoader.getMedicalRecords();
        if (medicalRecords.isEmpty()) {
            return Optional.empty();
        }

        Boolean exist = medicalRecords.stream()
                .anyMatch(m -> m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                        && m.getLastName().equalsIgnoreCase(medicalRecord.getLastName()));
        if (exist) {
            return Optional.empty();
        }
        medicalRecords.add(medicalRecord);
        dataLoader.saveData();
        return Optional.of(medicalRecord);
    }

    /**
     * Met à jour un dossier médical existant (identifié par prénom+nom).
     * Endpoint: PUT /medicalRecord
     *
     * @param medicalRecord Nouvelles infos, identifié par prénom+nom
     * @return Optional du MedicalRecord modifié, vide si non trouvé
     */
    //PUT - Modification d'un registre medical existant
    public Optional<MedicalRecord> updateMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord.getFirstName() == null || medicalRecord.getLastName() == null) {
            return Optional.empty();
        }
        List<MedicalRecord> medicalRecords = dataLoader.getMedicalRecords();
        if (medicalRecords.isEmpty()) {
            return Optional.empty();
        }

        Optional<MedicalRecord> resultOpt = medicalRecords.stream().filter(m ->
                m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName()) && m.getLastName().equalsIgnoreCase(medicalRecord.getLastName())).findFirst();

        resultOpt.ifPresent(r -> {
            r.setMedications(medicalRecord.getMedications());
            r.setBirthdate(medicalRecord.getBirthdate());
            r.setAllergies(medicalRecord.getAllergies());
            dataLoader.saveData();
        });
        return resultOpt;
    }

    /**
     * Supprime un dossier médical de la base de données, via prénom et nom.
     * Endpoint: DELETE /medicalRecord
     *
     * @param firstName prénom de la personne
     * @param lastName  nom de la personne
     * @return true si supprimé, false sinon
     */
    //DELETE : Supprimer un registre medical
    public Boolean deleteMedicalRecord(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return false;
        }
        List<MedicalRecord> medicalRecords = dataLoader.getMedicalRecords();
        if (medicalRecords.isEmpty()) {
            return false;
        }

        dataLoader.saveData();

        return medicalRecords.removeIf(m ->
                m.getLastName().equalsIgnoreCase(lastName) && m.getFirstName().equalsIgnoreCase(firstName));
    }
}
