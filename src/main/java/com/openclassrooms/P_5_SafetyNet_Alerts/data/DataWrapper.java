package com.openclassrooms.P_5_SafetyNet_Alerts.data;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import lombok.Data;
import java.util.List;

@Data
public class DataWrapper {
      private List<Person> persons;
      private List<Firestation> firestations;
      private List<MedicalRecord> medicalRecords;


}
