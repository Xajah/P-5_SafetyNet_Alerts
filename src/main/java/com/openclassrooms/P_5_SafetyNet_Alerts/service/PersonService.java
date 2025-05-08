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

        Optional<PersonsByFirestationIDReturn> result = Optional.of(PersonsByFirestationIDReturn.builder()
                .persons(dtos)
                .countOfAdults(ageCount.getOrDefault(true, 0L).intValue())
                .countOfChilds(ageCount.getOrDefault(false, 0L).intValue())
                .build());

        return result;
    }

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

    // -------------------- /fire?address=xxx --------------------- //
    public Optional<FireAddressReturnDTO> getHouseholdInfoByAddress(String address) {
        List<Person> persons = dataLoader.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .collect(Collectors.toList());
        Optional<Integer> firestationNumber = firestationService.getFirestationNumberByAddress(address);

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
    // -------------------- /phoneAlert?firestation=xx --------------------- //
    public Optional<PhoneAlertByFirestationDTO> getPhoneAlertByFirestation(int stationNumber) {
        List<String> addresses = firestationService.getAddressesByStationID(stationNumber);

        Set<String> phones = dataLoader.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Optional<PhoneAlertByFirestationDTO> result = Optional.of(PhoneAlertByFirestationDTO.builder()
                .phoneNumbers(new ArrayList<>(phones))
                .build());

        return result;
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
