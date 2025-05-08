package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FirestationService {

    private final DataLoader dataLoader;

    public FirestationService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public List<Firestation> getFirestations() {
        // DataLoader ne renvoie jamais null
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

}
