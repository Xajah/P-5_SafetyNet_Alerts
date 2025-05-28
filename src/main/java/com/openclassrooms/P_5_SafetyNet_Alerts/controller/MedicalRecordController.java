package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Ajout import logger
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *   Contrôleur REST pour la gestion des informations medicales dans l’application SafetyNet.
 *  Gère les endpoints CRUD pour les informations medicales.
 *  */

@RestController
@RequiredArgsConstructor
public class MedicalRecordController {

    private static final Logger logger = LogManager.getLogger(MedicalRecordController.class);

    private final MedicalRecordService medicalRecordService;

    /**
     * Ajoute un dossier médical.
     *
     * @param medicalRecord Dossier médical à ajouter
     * @return Le dossier ajouté (201), ou 409 si conflit (déjà existant, clef prenom + nom)
     */
    @PostMapping("/medicalRecord")
    public ResponseEntity<Optional<MedicalRecord>> addMedicalRecord(@RequestBody MedicalRecord medicalRecord){
        logger.info("POST /medicalRecord - Request: {}", medicalRecord);
        Optional<MedicalRecord> result = medicalRecordService.addMedicalRecord(medicalRecord);
        if (result.isPresent()){
            logger.info("POST /medicalRecord - CREATED: {}", result.get());
            return ResponseEntity.status(201).body(result);
        }
        logger.error("POST /medicalRecord - CONFLICT: {}", medicalRecord);
        return ResponseEntity.status(409).build();
    }

    /**
     * Met à jour un dossier médical existant.
     *
     * @param medicalRecord Dossier médical à mettre à jour (identifié par nom/prénom)
     * @return Le dossier modifié (200), ou 410 si non trouvé (clef prenom + nom)
     */
    @PutMapping("/medicalRecord")
    public ResponseEntity<Optional<MedicalRecord>> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord){
        logger.info("PUT /medicalRecord - Request: {}", medicalRecord);
        Optional<MedicalRecord> result = medicalRecordService.updateMedicalRecord(medicalRecord);
        if (result.isPresent()){
            logger.info("PUT /medicalRecord - UPDATED: {}", result.get());
            return ResponseEntity.status(200).body(result);
        }
        logger.error("PUT /medicalRecord - NOT FOUND: {}", medicalRecord);
        return  ResponseEntity.status(410).build();
    }

    /**
     * Supprime un dossier médical d'une personne à partir de son prénom et nom.
     *
     * @param firstName Prénom de la personne
     * @param lastName  Nom de la personne
     * @return 200 si supprimé, 410 si non trouvé
     */
    @DeleteMapping("/medicalRecord")
    public ResponseEntity<Void> deleteMedicalRecord (@RequestParam String firstName, @RequestParam String lastName){
        logger.info("DELETE /medicalRecord - firstName={}, lastName={}", firstName, lastName);
        Boolean delete = medicalRecordService.deleteMedicalRecord(firstName, lastName);
        if (delete){
            logger.info("DELETE /medicalRecord - DELETED: firstName={}, lastName={}", firstName, lastName);
            return ResponseEntity.status(200).build();
        }
        logger.error("DELETE /medicalRecord - NOT FOUND: firstName={}, lastName={}", firstName, lastName);
        return ResponseEntity.status(410).build();
    }
}
