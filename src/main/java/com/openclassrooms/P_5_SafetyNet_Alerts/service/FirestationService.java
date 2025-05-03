package com.openclassrooms.P_5_SafetyNet_Alerts.service;

import com.openclassrooms.P_5_SafetyNet_Alerts.data.DataLoader;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirestationService {

    private final DataLoader dataLoader;

    public List<Firestation> getFirestations() {
        return dataLoader.getFirestations();
    }
    public List<Firestation> getFirestationsByID(int id) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getStation() == id)
                .collect(Collectors.toList());
    }
    public List<String> getAddressesByStationID(int id) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getStation() == id)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
    }
    public Firestation getFirestationByAdress(String adress) {
        return dataLoader.getFirestations().stream()
                .filter(f -> f.getAddress().equals(adress))
                .findFirst()
                .orElse(null);
    }
    // Pour flood/stations (ensemble d'IDs)
    public List<String> getAddressesByStationIDs(List<Integer> ids) {
        return dataLoader.getFirestations().stream()
                .filter(f -> ids.contains(f.getStation()))
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());
    }
}
