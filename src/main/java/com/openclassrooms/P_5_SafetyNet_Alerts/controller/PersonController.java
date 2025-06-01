package com.openclassrooms.P_5_SafetyNet_Alerts.controller;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import com.openclassrooms.P_5_SafetyNet_Alerts.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// ----- Ajout des imports pour log4j -----
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *   Contrôleur REST pour la gestion des personnes dans l’application SafetyNet.
 *  Gère les endpoints CRUD pour les personnes, ainsi que les GETs de l'API.
 *  */
@RestController
@RequiredArgsConstructor
public class PersonController {

    private static final Logger logger = LogManager.getLogger(PersonController.class);

    private final PersonService personService;

    /**
     * Récupère la liste des personnes couvertes par la caserne dont l'identifiant est passé en paramètre.
     *
     * @param stationNumber numéro de la caserne
     * @return Les personnes couvertes, ou 404 si aucune trouvée
     */
    // -------------------- /firestation?stationNumber=xx --------------------- //
    @GetMapping("/firestation")
    public ResponseEntity<Optional<PersonsByFirestationIDReturn>> getPersonsByFirestationId(
            @RequestParam("stationNumber") int stationNumber) {
        logger.info("GET /firestation - stationNumber={}", stationNumber);
        Optional<PersonsByFirestationIDReturn> result = personService.getAllPersonsByDependingOfFirestationID(stationNumber);
        if (result.isEmpty()) {
            logger.error("GET /firestation - NOT FOUND for stationNumber={}", stationNumber);
            return ResponseEntity.status(404).build();
        }
        logger.info("GET /firestation - OK for stationNumber={}", stationNumber);
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne la liste des enfants de moins de 18 ans vivant à une adresse donnée,
     * ainsi que la liste des autres membres du foyer.
     *
     * @param address Adresse concernée
     * @return Liste d'enfants et membres de la famille, ou 404 si aucun enfant à cette adresse
     */
    // -------------------- /childAlert?address=xxx --------------------- //
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildAlertDTO>> getChildsByAddress(@RequestParam("address") String address) {
        logger.info("GET /childAlert - address={}", address);
        List<ChildAlertDTO> result = personService.getChildsByAdress(address);
        if (result.isEmpty()) {
            logger.error("GET /childAlert - NOT FOUND for address={}", address);
            return ResponseEntity.status(404).build();
        }
        logger.info("GET /childAlert - OK for address={}", address);
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère tous les numéros de téléphone desservis par la caserne demandée.
     *
     * @param firestation numéro de la caserne
     * @return List des téléphones ou 404 si vide
     */
    // -------------------- /phoneAlert?firestation=xx --------------------- //
    @GetMapping("/phoneAlert")
    public ResponseEntity<Optional<PhoneAlertByFirestationDTO>> getPhonesByFirestationID(@RequestParam int firestation){
        logger.info("GET /phoneAlert - firestation={}", firestation);
        Optional<PhoneAlertByFirestationDTO> result = personService.getPhoneAlertByFirestation(firestation);
        if(result.isEmpty()){
            logger.error("GET /phoneAlert - NOT FOUND for firestation={}", firestation);
            return ResponseEntity.status(404).body(result);
        }
        logger.info("GET /phoneAlert - OK for firestation={}", firestation);
        return ResponseEntity.ok(result);
    }

    /**
     * Affiche la liste des habitants d’une adresse donnée ainsi que le numéro de la caserne correspondante.
     *
     * @param address Adresse à interroger
     * @return Liste des habitants avec infos médicales et caserne, ou 404 si non trouvée
     */
    // -------------------- /fire?address=xxx --------------------- //
    @GetMapping("/fire")
    public ResponseEntity<Optional<FireAddressReturnDTO>> getStationAndPeopleForAFire(@RequestParam String address){
        logger.info("GET /fire - address={}", address);
        Optional<FireAddressReturnDTO> result = personService.getHouseholdInfoByAddress(address);
        if (result.isEmpty()){
            logger.error("GET /fire - NOT FOUND for address={}", address);
            return ResponseEntity.status(404).build();}
        logger.info("GET /fire - OK for address={}", address);
        return ResponseEntity.ok(result);
    }

    /**
     * Pour plusieurs numéros de casernes donnés, retourne la liste des foyers à couvrir en cas d'inondation.
     *
     * @param stationNumbers Liste des numéros de casernes
     * @return Map des adresses et liste des habitants, ou 404 si rien trouvé
     */
    // -------------------- /flood/stations?stations=xx,yy,zz --------------------- //
    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<FireAddressResidentDTO>>> getFloodInfoByStations(
            @RequestParam("stations") List<Integer> stationNumbers) {
        logger.info("GET /flood/stations - stations={}", stationNumbers);
        Map<String, List<FireAddressResidentDTO>> result = personService.getFloodInfoByStations(stationNumbers);
        if (result.isEmpty()) {
            logger.error("GET /flood/stations - NOT FOUND for stations={}", stationNumbers);
            return ResponseEntity.status(404).body(result);
        }
        logger.info("GET /flood/stations - OK for stations={}", stationNumbers);
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne les informations d'une ou plusieurs personnes portant ce nom de famille.
     *
     * @param lastName Nom de famille à rechercher
     * @return Liste d'informations ou 404 si personne trouvée
     */
    // -------------------- /personInfo?lastName=xxx --------------------- //
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoByNameDTO>> getPersonInfoByName(@RequestParam("lastName") String lastName) {
        logger.info("GET /personInfo - lastName={}", lastName);
        List<PersonInfoByNameDTO> result = personService.getPersonsInfoByLastName(lastName);
        if (result.isEmpty()) {
            logger.error("GET /personInfo - NOT FOUND for lastName={}", lastName);
            return ResponseEntity.status(404).body(result);
        }
        logger.info("GET /personInfo - OK for lastName={}", lastName);
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne toutes les adresses emails pour une ville donnée.
     *
     * @param city Nom de la ville
     * @return Liste des emails ou 404 si aucune trouvée
     */
    // -------------------- /communityEmail?city=xxx --------------------- //
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getEmailsByCity(@RequestParam("city") String city) {
        logger.info("GET /communityEmail - city={}", city);
        List<String> emails = personService.getEmailsByCity(city);
        if (emails.isEmpty()) {
            logger.error("GET /communityEmail - NOT FOUND for city={}", city);
            return ResponseEntity.status(404).body(emails);
        }
        logger.info("GET /communityEmail - OK for city={}", city);
        return ResponseEntity.ok(emails);

        //-----------EndPoint----------//
    }

    /**
     * Ajoute une personne à la base de données si elle n'existe pas déjà (même prenom et nom).
     *
     * @param person Personne à ajouter
     * @return La personne en cas de succès (201) ou 409 si introuvable
     */
    @PostMapping("/person")
    public  ResponseEntity<Optional<Person>> addPerson(@RequestBody Person person){
        logger.info("POST /person - Request: {}", person);
        Optional<Person> result = personService.addPerson(person);
        if(result.isPresent()){
            logger.info("POST /person - CREATED: {}", person);
            return ResponseEntity.status(201).body(result);
        }
        logger.error("POST /person - CONFLICT: {}", person);
        return ResponseEntity.status(409).build();
    }

    /**
     * Met à jour une personne.
     *
     * @param person Personne à mettre à jour (identifiée par prénom et nom)
     * @return Personne modifiée (200) ou erreur si non trouvée (410)
     */
    @PutMapping("/person")
    public ResponseEntity<Optional<Person>> updatePerson(@RequestBody Person person){
        logger.info("PUT /person - Request: {}", person);
        Optional<Person> result = personService.updatePerson(person);
        if (result.isPresent()){
            logger.info("PUT /person - UPDATED: {}", person);
            return ResponseEntity.status(200).body(result);
        }
        logger.error("PUT /person - NOT FOUND: {}", person);
        return ResponseEntity.status(410).build();

    }

    /**
     * Supprime une personne par son prénom et son nom.
     *
     * @param firstName Prénom de la personne à supprimer
     * @param lastName Nom de la personne à supprimer
     * @return 200 si suppression réalisée, 410 si personne non trouvée
     */
    @DeleteMapping("/person")
    public  ResponseEntity<Void> deletePerson (@RequestParam String firstName,@RequestParam String lastName){
        logger.info("DELETE /person - firstName={}, lastName={}", firstName, lastName);
        boolean delete = personService.deletePerson(firstName, lastName);
        if(delete){
            logger.info("DELETE /person - DELETED: firstName={}, lastName={}", firstName, lastName);
            return ResponseEntity.status(200).build();
        }
        logger.error("DELETE /person - NOT FOUND: firstName={}, lastName={}", firstName, lastName);
        return ResponseEntity.status(410).build();
    }
}
