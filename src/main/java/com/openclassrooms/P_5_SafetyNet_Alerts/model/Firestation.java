package com.openclassrooms.P_5_SafetyNet_Alerts.model;



import lombok.Builder;
import  lombok.Data;

@Data
@Builder
public class Firestation {

    private String address;
    private int station;

}
