package com.openclassrooms.P_5_SafetyNet_Alerts.model;



import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class MedicalRecord {
    private String firstName;
    private String lastName;
    private Date birthdate;
    private List<String> medications;
    private List<String> allergies;

}
