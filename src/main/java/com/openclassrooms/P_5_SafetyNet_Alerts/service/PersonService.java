package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import com.openclassrooms.P_5_SafetyNet_Alerts.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service métier dédié à la gestion des personnes ({@link Person}).
 * <p>
 * Permet de répondre aux besoins de l'application en matière de recherche, création,
 * modification et suppression d'habitants, et de fournir des informations ou agrégations
 * utiles aux contrôleurs REST (par exemple: habitants desservis par une caserne, enfants par adresse,
 * informations pour alertes, filtres par ville, etc.).
 */

@Service
@RequiredArgsConstructor
public class PersonService {

    private final DataLoader dataLoader;
    private final MedicalRecordService medicalRecordService;
    private final FirestationService firestationService;

    /**
     * Récupère la liste des personnes couvertes par une caserne particulière.
     * <p>
     * Endpoint GET
     * Retourne les habitants couverts, avec prénom, nom, adresse, téléphone,
     * et un décompte des adultes et des enfants (-18 ans) dans la zone desservie.
     *
     * @param stationId Numéro de la caserne
     * @return Objet contenant la liste des personnes et les comptes adultes/enfants,
     * ou Optional.empty() si aucune trouvée
     */
    // -------------------- /firestation?stationNumber=xx --------------------- //
    public Optional<PersonsByFirestationIDReturn> getAllPersonsByDependingOfFirestationID(int stationId) {
        List<String> addresses = firestationService.getAddressesByStationID(stationId);

        List<Person> coveredPersons = dataLoader.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());

        List<PersonByFirestationID> dtos = coveredPersons.stream()
                .map(p -> PersonByFirestationID.builder()
                        .lastName(p.getLastName())
                        .firstName(p.getFirstName())
                        .adress(p.getAddress())
                        .phoneNumber(p.getPhone())
                        .build())
                .collect(Collectors.toList());

        Map<Boolean, Long> ageCount = coveredPersons.stream()
                .map(p -> medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName())
                        .map(MedicalRecord::getBirthdate)
                        .map(DateUtils::calculateAgeFromBirthdate)
                        .orElse(0))
                .collect(Collectors.partitioningBy(age -> age >= 18, Collectors.counting()));

        if (dtos.isEmpty()) {
            return Optional.empty();
        }

        Optional<PersonsByFirestationIDReturn> result = Optional.of(PersonsByFirestationIDReturn.builder()
                .persons(dtos)
                .countOfAdults(ageCount.getOrDefault(true, 0L).intValue())
                .countOfChilds(ageCount.getOrDefault(false, 0L).intValue())
                .build());

        return result;
    }

    /**
     * Récupère une liste de tous les enfants (- 18 ans) d'une adresse donnée,
     * ainsi que la liste des autres membres du foyer.
     * <p>
     * Endpoint GET
     * Retourne les enfants, leur âge, et les autres membres du foyer.
     * Si aucun enfant, la liste retournée est vide.
     *
     * @param address Adresse recherchée
     * @return Liste d'enfants à l'adresse (avec membres du foyer)
     */
    // -------------------- /childAlert?address=xxx --------------------- //
    public List<ChildAlertDTO> getChildsByAdress(String address) {
        List<Person> residents = dataLoader.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .collect(Collectors.toList());

        List<HouseholdMemberDTO> members = residents.stream()
                .map(p -> HouseholdMemberDTO.builder()
                        .firstName(p.getFirstName())
                        .lastName(p.getLastName())
                        .build())
                .collect(Collectors.toList());

        return residents.stream()
                .filter(p -> medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName())
                        .map(MedicalRecord::getBirthdate)
                        .map(DateUtils::calculateAgeFromBirthdate)
                        .orElse(0) < 18)
                .map(p -> {
                    Optional<MedicalRecord> mr = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    int age = mr.map(MedicalRecord::getBirthdate)
                            .map(DateUtils::calculateAgeFromBirthdate)
                            .orElse(0);
                    List<HouseholdMemberDTO> otherMembers = members.stream()
                            .filter(m -> !(m.getFirstName().equals(p.getFirstName()) && m.getLastName().equals(p.getLastName())))
                            .collect(Collectors.toList());
                    return ChildAlertDTO.builder()
                            .firstName(p.getFirstName())
                            .lastName(p.getLastName())
                            .age(age)
                            .householdMembers(otherMembers)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Donne la liste des habitants d'une adresse ainsi que le numéro de la caserne les desservant.
     * <p>
     * Endpoint  GET
     * Retourne la liste des habitants (nom, téléphone, âge, antécédents médicaux).
     *
     * @param address Adresse à rechercher
     * @return DTO avec numéro de caserne et liste des résidents (ou Optional.empty())
     */
    // -------------------- /fire?address=xxx --------------------- //
    public Optional<FireAddressReturnDTO> getHouseholdInfoByAddress(String address) {
        List<Person> persons = dataLoader.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .collect(Collectors.toList());
        Optional<Integer> firestationNumber = firestationService.getFirestationNumberByAddress(address);

        if (persons.isEmpty() && firestationNumber.isEmpty()) {
            return Optional.empty();
        }

        List<FireAddressResidentDTO> residents = persons.stream()
                .map(p -> {
                    Optional<MedicalRecord> record = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    return FireAddressResidentDTO.builder()
                            .lastName(p.getLastName())
                            .firstName(p.getFirstName())
                            .phone(p.getPhone())
                            .age(record.map(MedicalRecord::getBirthdate).map(DateUtils::calculateAgeFromBirthdate).orElse(0))
                            .medications(record.map(MedicalRecord::getMedications).orElse(Collections.emptyList()))
                            .allergies(record.map(MedicalRecord::getAllergies).orElse(Collections.emptyList()))
                            .build();
                })
                .collect(Collectors.toList());
        Optional<FireAddressReturnDTO> result = Optional.of(FireAddressReturnDTO.builder()
                .stationNumber(firestationNumber.orElse(0))
                .residents(residents)
                .build());
        return result;
    }

    /**
     * Récupère la liste des numéros de téléphone des résidents couverts par une caserne donnée.
     * <p>
     * Endpoint GET
     * Sert à prévenir par SMS en cas d'urgence.
     *
     * @param stationNumber Numéro de la caserne
     * @return DTO contenant la liste des numéros, ou Optional.empty() si aucun trouvé
     */
    // -------------------- /phoneAlert?firestation=xx --------------------- //
    public Optional<PhoneAlertByFirestationDTO> getPhoneAlertByFirestation(int stationNumber) {
        List<String> addresses = firestationService.getAddressesByStationID(stationNumber);

        Set<String> phones = dataLoader.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (phones.isEmpty()) {
            return Optional.empty();
        }
        Optional<PhoneAlertByFirestationDTO> result = Optional.of(PhoneAlertByFirestationDTO.builder()
                .phoneNumbers(new ArrayList<>(phones))
                .build());

        return result;
    }

    /**
     * Récupère les informations de tous les foyers desservis par une liste de casernes.
     * <p>
     * Endpoint  GET
     * Retourne une map adresse -> liste de résidents (nom, téléphone, âge, antécédents médicaux).
     *
     * @param stationNumbers Liste de numéros de casernes à couvrir
     * @return Map adresse -> liste de résidents avec infos
     */
    // -------------------- /flood/stations?stations=xx,yy,zz --------------------- //
    public Map<String, List<FireAddressResidentDTO>> getFloodInfoByStations(List<Integer> stationNumbers) {
        List<String> addresses = firestationService.getAddressesByStationIDs(stationNumbers);
        return addresses.stream()
                .collect(Collectors.toMap(
                        address -> address,
                        address -> dataLoader.getPersons().stream()
                                .filter(p -> p.getAddress().equals(address))
                                .map(p -> {
                                    Optional<MedicalRecord> record = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                                    return FireAddressResidentDTO.builder()
                                            .lastName(p.getLastName())
                                            .firstName(p.getFirstName())
                                            .phone(p.getPhone())
                                            .age(record.map(MedicalRecord::getBirthdate).map(DateUtils::calculateAgeFromBirthdate).orElse(0))
                                            .medications(record.map(MedicalRecord::getMedications).orElse(Collections.emptyList()))
                                            .allergies(record.map(MedicalRecord::getAllergies).orElse(Collections.emptyList()))
                                            .build();
                                })
                                .collect(Collectors.toList())
                ));
    }

    /**
     * Récupère toutes les personnes portant un certain nom de famille,
     * avec leur nom, adresse, âge, email, antécédents médicaux.
     * <p>
     * Endpoint  GET
     *
     * @param lastName Nom de famille à rechercher
     * @return Liste d'informations pour chaque personne trouvée
     */
    // -------------------- /personInfo?lastName=xxx --------------------- //
    public List<PersonInfoByNameDTO> getPersonsInfoByLastName(String lastName) {
        return dataLoader.getPersons().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .map(p -> {
                    Optional<MedicalRecord> record = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    return PersonInfoByNameDTO.builder()
                            .firstName(p.getFirstName())
                            .lastName(p.getLastName())
                            .address(p.getAddress())
                            .email(p.getEmail())
                            .age(record.map(MedicalRecord::getBirthdate).map(DateUtils::calculateAgeFromBirthdate).orElse(0))
                            .medications(record.map(MedicalRecord::getMedications).orElse(Collections.emptyList()))
                            .allergies(record.map(MedicalRecord::getAllergies).orElse(Collections.emptyList()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les adresses email de tous les habitants d'une ville.
     * <p>
     * Endpoint  GET
     *
     * @param city Ville à rechercher
     * @return Liste d'emails (sans doublons)
     */
    // -------------------- /communityEmail?city=xxx --------------------- //
    public List<String> getEmailsByCity(String city) {
        return dataLoader.getPersons().stream()
                .filter(p -> city.equalsIgnoreCase(p.getCity()))
                .map(Person::getEmail)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }


    //-------------------------------------------------/EndPoints/----------------------------------------------//

    /**
     * Ajoute une nouvelle personne à la base de données.
     * <p>
     * Endpoint : POST /person
     *
     * @param person Personne à ajouter
     * @return Optional avec la personne ajoutée, ou vide si déjà existant ou invalide
     */
    // POST : Ajouter une nouvelle personne
    public Optional<Person> addPerson(Person person) {
        if (person.getFirstName() == null || person.getLastName() == null) {
            return Optional.empty();
        }
        List<Person> persons = dataLoader.getPersons();
        if (persons.isEmpty()) {
            return Optional.empty();
        }

        boolean exists = persons.stream()
                .anyMatch(p -> p.getFirstName().equalsIgnoreCase(person.getFirstName())
                        && p.getLastName().equalsIgnoreCase(person.getLastName()));
        if (exists) {
            return Optional.empty();
        }
        persons.add(person);
        dataLoader.saveData();
        return Optional.of(person);
    }

    /**
     * Met à jour les champs d'une personne existante (hors prénom et nom).
     * <p>
     * Endpoint : PUT /person
     *
     * @param person Identifiant par prénom/nom et nouvelles valeurs à mettre à jour
     * @return Optional avec la personne mise à jour, ou vide si non trouvée
     */
    // PUT : Mettre à jour une personne existante (hors prénom et nom)
    public Optional<Person> updatePerson(Person person) {
        List<Person> persons = dataLoader.getPersons();
        if (persons.isEmpty()) {
            return Optional.empty();
        }
        Optional<Person> existingOpt = persons.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(person.getFirstName())
                        && p.getLastName().equalsIgnoreCase(person.getLastName()))
                .findFirst();

        existingOpt.ifPresent(p -> {
            p.setAddress(person.getAddress());
            p.setCity(person.getCity());
            p.setEmail(person.getEmail());
            p.setPhone(person.getPhone());
            dataLoader.saveData();
        });
        return existingOpt;
    }

    /**
     * Supprime une personne de la base (par prénom et nom).
     * <p>
     * Endpoint : DELETE /person
     *
     * @param firstName Prénom à supprimer
     * @param lastName  Nom à supprimer
     * @return true si supprimée, false si introuvable
     */
    // DELETE : Supprimer une personne (clé nom/prénom)
    public boolean deletePerson(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return false;
        }
        List<Person> persons = dataLoader.getPersons();
        if (persons.isEmpty()) {
            return false;
        }
        dataLoader.saveData();

        return persons.removeIf(p -> p.getFirstName().equalsIgnoreCase(firstName)
                && p.getLastName().equalsIgnoreCase(lastName));
    }
}
