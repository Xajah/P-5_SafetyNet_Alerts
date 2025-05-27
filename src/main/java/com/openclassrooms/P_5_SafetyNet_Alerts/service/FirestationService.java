package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirestationService {

    private final DataLoader dataLoader;


    public List<Firestation> getFirestations() {
        return dataLoader.getFirestations();
    }

    public Optional<Firestation> getFirestationByAdress(String address) {
        return dataLoader.getFirestations()
                .stream()
                .filter(f -> f.getAddress().equalsIgnoreCase(address))
                .findFirst();
    }

    public List<Firestation> getFirestationsByID(int stationID) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getStation() == stationID)
                .collect(Collectors.toList());
    }

    public List<String> getAddressesByStationID(int stationID) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getStation() == stationID)
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAddressesByStationIDs(List<Integer> stationIDs) {
        if (stationIDs == null || stationIDs.isEmpty()) return Collections.emptyList();
        return dataLoader.getFirestations().stream()
                .filter(f -> stationIDs.contains(f.getStation()))
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());
    }
    public Optional<Integer> getFirestationNumberByAddress(String address) {
        // Renvoie un Optional du numéro de station associé à l'adresse
        return getFirestationByAdress(address).map(Firestation::getStation);
    }

//-------------------------------------------------/EndPoints/----------------------------------------------//
// POST : Ajouter une nouvelle Station
public Optional<Firestation> addFirestation(Firestation firestation){
    if(firestation.getAddress()== null ){return Optional.empty();}
    List<Firestation> firestations = dataLoader.getFirestations();
    if(firestations.isEmpty()){return Optional.empty();}

    Boolean exist = firestations.stream()
            .anyMatch(f -> f.getAddress().equalsIgnoreCase(firestation.getAddress()));
    if (exist) {
        return Optional.empty();
    }
    firestations.add(firestation);
    return Optional.of(firestation);
}
   // Put : Mettre à jour une station existante
    public Optional<Firestation> updateFirestation (Firestation firestation){
        if(firestation.getAddress()== null ){return Optional.empty();}
        List<Firestation> firestations = dataLoader.getFirestations();
        if(firestations.isEmpty()){return Optional.empty();}
         Optional<Firestation> resultOpt = firestations.stream().filter(f -> f.getAddress().equalsIgnoreCase(firestation.getAddress())).findFirst();
         resultOpt.ifPresent(f -> f.setStation(firestation.getStation()));
         return resultOpt;
    }

    //DELETE : Supprimer le mapping d'une caserne ou d'une adresse
    public Boolean deleteFirestationMappingByAdress (String adress){
        if(adress== null ){return false;}
        List<Firestation> firestations = dataLoader.getFirestations();
        if(firestations.isEmpty()){return false;}
        return firestations.removeIf(f -> f.getAddress().equalsIgnoreCase(adress));
    }
    public Boolean deleteFirestationMappingById (Integer id){
        List<Firestation> firestations = dataLoader.getFirestations();
        if(firestations.isEmpty()){return false;}
        return firestations.removeIf(f -> f.getStation() == id);
    }

}
