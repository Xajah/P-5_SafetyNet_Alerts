package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO.*;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final DataLoader dataLoader;
    private final MedicalRecordService medicalRecordService;
    private final FirestationService firestationService;

    // -------------------- /firestation?stationNumber=xx --------------------- //
    public PersonsByFirestationIDReturn getAllPersonsByDependingOfFirestationID(int stationId) {
        // Récupération des adresses couvertes par la caserne
        List<String> addresses = firestationService.getAddressesByStationID(stationId);

        // Filtrer toutes les personnes habitant à ces adresses
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

        // Partitionner les âges en adulte et enfant
        Map<Boolean, Long> ageCount = coveredPersons.stream()
                .map(p -> {
                    MedicalRecord mr = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    if (mr != null) {
                        return DateUtils.calculateAgeFromBirthdate(mr.getBirthdate());
                    }
                    return -1;
                })
                .collect(Collectors.partitioningBy(age -> age >= 18, Collectors.counting()));

        return PersonsByFirestationIDReturn.builder()
                .persons(dtos)
                .countOfAdults(ageCount.getOrDefault(true, 0L).intValue())
                .countOfChilds(ageCount.getOrDefault(false, 0L).intValue())
                .build();
    }

    // -------------------- /childAlert?address=xxx --------------------- //
    public List<ChildAlertDTO> getChildsByAdress(String address) {
        List<Person> residents = dataLoader.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .collect(Collectors.toList());

        // Pré-calcul des autres membres du foyer
        List<HouseholdMemberDTO> members = residents.stream()
                .map(p -> HouseholdMemberDTO.builder()
                        .firstName(p.getFirstName())
                        .lastName(p.getLastName())
                        .build())
                .collect(Collectors.toList());

        return residents.stream()
                .filter(p -> {
                    MedicalRecord mr = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    return mr != null && DateUtils.calculateAgeFromBirthdate(mr.getBirthdate()) < 18;
                })
                .map(p -> {
                    MedicalRecord mr = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    int age = DateUtils.calculateAgeFromBirthdate(mr.getBirthdate());
                    // Liste des membres hors cet enfant
                    List<HouseholdMemberDTO> otherMembers = members.stream()
                            .filter(m -> !(m.getFirstName().equals(p.getFirstName())
                                    && m.getLastName().equals(p.getLastName())))
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

    // -------------------- /phoneAlert?firestation=xx --------------------- //
    public PhoneAlertByFirestationDTO getPhoneAlertByFirestation(int stationNumber) {
        List<String> addresses = firestationService.getAddressesByStationID(stationNumber);

        Set<String> phones = dataLoader.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return PhoneAlertByFirestationDTO.builder()
                .phoneNumbers(new ArrayList<>(phones))
                .build();
    }

    // -------------------- /fire?address=xxx --------------------- //
    public FireAddressReturnDTO getHouseholdInfoByAddress(String address) {
        Firestation firestation = firestationService.getFirestationByAdress(address);
        if (firestation == null) {
            return null;
        }
        int firestationNumber = firestation.getStation();

        List<FireAddressResidentDTO> residents = dataLoader.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .map(p -> {
                    MedicalRecord record = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    return FireAddressResidentDTO.builder()
                            .lastName(p.getLastName())
                            .firstName(p.getFirstName())
                            .phone(p.getPhone())
                            .age(record != null ? DateUtils.calculateAgeFromBirthdate(record.getBirthdate()) : 0)
                            .medications(record != null ? record.getMedications() : Collections.emptyList())
                            .allergies(record != null ? record.getAllergies() : Collections.emptyList())
                            .build();
                })
                .collect(Collectors.toList());

        return FireAddressReturnDTO.builder()
                .stationNumber(firestationNumber)
                .residents(residents)
                .build();
    }

    // -------------------- /flood/stations?stations=xx,yy,zz --------------------- //
    public Map<String, List<FireAddressResidentDTO>> getFloodInfoByStations(List<Integer> stationNumbers) {
        List<String> addresses = firestationService.getAddressesByStationIDs(stationNumbers);
        return addresses.stream()
                .collect(Collectors.toMap(
                        address -> address,
                        address -> dataLoader.getPersons().stream()
                                .filter(p -> p.getAddress().equals(address))
                                .map(p -> {
                                    MedicalRecord record = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                                    return FireAddressResidentDTO.builder()
                                            .lastName(p.getLastName())
                                            .firstName(p.getFirstName())
                                            .phone(p.getPhone())
                                            .age(record != null ? DateUtils.calculateAgeFromBirthdate(record.getBirthdate()) : 0)
                                            .medications(record != null ? record.getMedications() : Collections.emptyList())
                                            .allergies(record != null ? record.getAllergies() : Collections.emptyList())
                                            .build();
                                })
                                .collect(Collectors.toList())
                ));
    }

    // -------------------- /personInfo?lastName=xxx --------------------- //
    public List<PersonInfoByNameDTO> getPersonsInfoByLastName(String lastName) {
        return dataLoader.getPersons().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .map(p -> {
                    MedicalRecord record = medicalRecordService.getMedicalRecordByName(p.getFirstName(), p.getLastName());
                    return PersonInfoByNameDTO.builder()
                            .firstName(p.getFirstName())
                            .lastName(p.getLastName())
                            .address(p.getAddress())
                            .email(p.getEmail())
                            .age(record != null ? DateUtils.calculateAgeFromBirthdate(record.getBirthdate()) : 0)
                            .medications(record != null ? record.getMedications() : Collections.emptyList())
                            .allergies(record != null ? record.getAllergies() : Collections.emptyList())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // -------------------- /communityEmail?city=xxx --------------------- //
    public List<String> getEmailsByCity(String city) {
        return dataLoader.getPersons().stream()
                .filter(p -> city.equalsIgnoreCase(p.getCity()))
                .map(Person::getEmail)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
