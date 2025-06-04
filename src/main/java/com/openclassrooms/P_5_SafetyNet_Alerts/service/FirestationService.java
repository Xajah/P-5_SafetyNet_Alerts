package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service métier dédié à la gestion des mappings adresse-caserne ({@link Firestation}).
 * <p>
 * Permet de répondre aux besoins de l'application en matière de recherche, création,
 * modification et suppression de liaisons entre adresses et numéros de caserne, ainsi que
 * de fournir des listes utiles aux contrôleurs REST (par exemple: adresses par numéro de caserne,
 * caserne pour une adresse donnée, etc.).
 */

@Service
@RequiredArgsConstructor
public class FirestationService {

    private final DataLoader dataLoader;

    /**
     * Retourne la liste de toutes les mappings caserne/adresse.
     *
     * @return Liste de Firestation (mapping adresse/station)
     */
    public List<Firestation> getFirestations() {
        return dataLoader.getFirestations();
    }

    /**
     * Recherche la caserne associée à une adresse donnée.
     *
     * @param address l’adresse recherchée
     * @return Un Optional Firestation pour cette adresse
     */
    public Optional<Firestation> getFirestationByAdress(String address) {
        return dataLoader.getFirestations()
                .stream()
                .filter(f -> f.getAddress().equalsIgnoreCase(address))
                .findFirst();
    }

    /**
     * Recherche toutes les adresses desservies par une caserne donnée (par ID).
     *
     * @param stationID Numéro de la caserne
     * @return Liste d’objets Firestation pour ce numéro de station
     */
    public List<Firestation> getFirestationsByID(int stationID) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getStation() == stationID)
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste des adresses couvertes par une station donnée.
     * Utilisé principalement pour /firestation et /phoneAlert.
     *
     * @param stationID Numéro de la caserne
     * @return Liste d’adresses associées à cette caserne
     */
    public List<String> getAddressesByStationID(int stationID) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getStation() == stationID)
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste des adresses couvertes par une liste de casernes (ex : flood).
     *
     * @param stationIDs Liste d’identifiants de casernes
     * @return Liste d’adresses associées à ces casernes
     */
    public List<String> getAddressesByStationIDs(List<Integer> stationIDs) {
        if (stationIDs == null || stationIDs.isEmpty()) return Collections.emptyList();
        return dataLoader.getFirestations().stream()
                .filter(f -> stationIDs.contains(f.getStation()))
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Retourne le numéro de station pour une adresse donnée.
     *
     * @param address Adresse recherchée
     * @return Numéro de la caserne associée (ou vide si aucune)
     */
    public Optional<Integer> getFirestationNumberByAddress(String address) {
        // Renvoie un Optional du numéro de station associé à l'adresse
        return getFirestationByAdress(address).map(Firestation::getStation);
    }

    //-------------------------------------------------/EndPoints/----------------------------------------------//

    /**
     * Ajoute un nouveau mapping caserne/adresse.
     * Endpoint : POST /firestation
     *
     * @param firestation L’objet mapping adresse/station à ajouter
     * @return Optional avec le mapping ajouté, ou vide si déjà existant ou invalide
     */
    // POST : Ajouter une nouvelle Station
    public Optional<Firestation> addFirestation(Firestation firestation) {
        if (firestation.getAddress() == null) {
            return Optional.empty();
        }
        List<Firestation> firestations = dataLoader.getFirestations();
        if (firestations.isEmpty()) {
            return Optional.empty();
        }

        Boolean exist = firestations.stream()
                .anyMatch(f -> f.getAddress().equalsIgnoreCase(firestation.getAddress()));
        if (exist) {
            return Optional.empty();
        }
        firestations.add(firestation);
        dataLoader.saveData();
        return Optional.of(firestation);
    }

    /**
     * Met à jour le numéro de la caserne pour une adresse existante.
     * Endpoint : PUT /firestation
     *
     * @param firestation Mapping à mettre à jour (identifié par l’adresse)
     * @return Optional avec le mapping mis à jour, ou vide si non trouvé
     */
    // Put : Mettre à jour une station existante
    public Optional<Firestation> updateFirestation(Firestation firestation) {
        if (firestation.getAddress() == null) {
            return Optional.empty();
        }
        List<Firestation> firestations = dataLoader.getFirestations();
        if (firestations.isEmpty()) {
            return Optional.empty();
        }
        Optional<Firestation> resultOpt = firestations.stream().filter(f -> f.getAddress().equalsIgnoreCase(firestation.getAddress())).findFirst();
        resultOpt.ifPresent(f -> {
            f.setStation(firestation.getStation());
            dataLoader.saveData(); // Ajouté ici
        });
        return resultOpt;
    }

    /**
     * Supprime un mapping caserne/adresse à partir de l’adresse.
     * Endpoint : DELETE /firestation?address=xxx
     *
     * @param adress Adresse à retirer du mapping
     * @return true si supprimé, false sinon
     */
    //DELETE : Supprimer le mapping d'une caserne ou d'une adresse
    public Boolean deleteFirestationMappingByAdress(String adress) {
        if (adress == null) {
            return false;
        }
        List<Firestation> firestations = dataLoader.getFirestations();
        if (firestations.isEmpty()) {
            return false;
        }

        dataLoader.saveData();
        return firestations.removeIf(f -> f.getAddress().equalsIgnoreCase(adress));
    }

    /**
     * Supprime tous les mappings pour un numéro de caserne donné.
     * Endpoint : DELETE /firestation?firestation=xx
     *
     * @param id Numéro de station à retirer
     * @return true si au moins un mapping a été supprimé, false sinon
     */
    public Boolean deleteFirestationMappingById(Integer id) {
        List<Firestation> firestations = dataLoader.getFirestations();
        if (firestations.isEmpty()) {
            return false;
        }
        dataLoader.saveData();
        return firestations.removeIf(f -> f.getStation() == id);
    }
}
