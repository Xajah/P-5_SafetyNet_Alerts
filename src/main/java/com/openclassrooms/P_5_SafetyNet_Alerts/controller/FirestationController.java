package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.FirestationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des casernes dans l’application SafetyNet.
 * Gère les endpoints CRUD pour les casernes.
 */

@RestController
@RequiredArgsConstructor
public class FirestationController {


    private static final Logger logger = LogManager.getLogger(FirestationController.class);

    private final FirestationService firestationService;

    /**
     * Ajoute une nouvelle caserne.
     *
     * @param firestation Caserne à ajouter
     * @return La caserne créée (201), ou 409 si elle existe déjà (verifié par adresse)
     */
    //post
    @PostMapping("/firestation")
    public ResponseEntity<Firestation> addFirestation(@RequestBody Firestation firestation) {
        logger.info("POST /firestation - Request: {}", firestation);
        Optional<Firestation> result = firestationService.addFirestation(firestation);
        if (result.isPresent()) {
            logger.info("POST /firestation - CREATED: {}", result.get());
            return ResponseEntity.status(201).body(result.get());
        }
        logger.error("POST /firestation - CONFLICT: {}", firestation);
        return ResponseEntity.status(409).build();
    }

    /**
     * Met à jour une caserne existante (adresse comme clef).
     *
     * @param firestation Caserne à mettre à jour
     * @return La caserne mise à jour (200), ou 410 si échec
     */
    @PutMapping("/firestation")
    public ResponseEntity<Firestation> updateFirestation(@RequestBody Firestation firestation) {
        logger.info("PUT /firestation - Request: {}", firestation);
        Optional<Firestation> result = firestationService.updateFirestation(firestation);
        if (result.isPresent()) {
            logger.info("PUT /firestation - UPDATED: {}", result.get());
            return ResponseEntity.status(200).body(result.get());
        }
        logger.error("PUT /firestation - NOT FOUND: {}", firestation);
        return ResponseEntity.status(410).build();
    }

    /**
     * Supprime la caserne par son identifiant firestation.
     *
     * @param firestation Identifiant de la caserne
     * @return 200 si supprimée, 410 si échec
     */
    @DeleteMapping(path = "/firestation", params = "firestation")
    public ResponseEntity<Void> deleteFirestationWithID(@RequestParam int firestation) {
        logger.info("DELETE /firestation?firestation={} - Request", firestation);
        boolean deleted = firestationService.deleteFirestationMappingById(firestation);
        if (deleted) {
            logger.info("DELETE /firestation?firestation={} - DELETED", firestation);
            return ResponseEntity.status(200).build();
        } else {
            logger.error("DELETE /firestation?firestation={} - NOT FOUND", firestation);
            return ResponseEntity.status(410).build();
        }
    }

    /**
     * Supprime la caserne par son adresse.
     *
     * @param address Adresse à supprimer
     * @return 200 si supprimée, 410 si échec
     */
    @DeleteMapping(path = "/firestation", params = "address")
    public ResponseEntity<Void> deleteFirestationWithAdresse(@RequestParam String address) {
        logger.info("DELETE /firestation?address={} - Request", address);
        boolean deleted = firestationService.deleteFirestationMappingByAdress(address);
        if (deleted) {
            logger.info("DELETE /firestation?address={} - DELETED", address);
            return ResponseEntity.status(200).build();
        } else {
            logger.error("DELETE /firestation?address={} - NOT FOUND", address);
            return ResponseEntity.status(410).build();
        }
    }
}
