package com.openclassrooms.P_5_SafetyNet_Alerts.data;

import com.openclassrooms.P_5_SafetyNet_Alerts.model.Firestation;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.MedicalRecord;
import com.openclassrooms.P_5_SafetyNet_Alerts.model.Person;
import lombok.Data;
import java.util.List;

/**
 * Classe utilitaire pour l'utilisation de Jackson, servant à encapsuler les listes d'objets métier
 * extraites du fichier JSON de données de l'application SafetyNet Alerts.
 * <p>
 * Permet le mapping direct avec la structure globale du fichier JSON.
 */
@Data
public class DataWrapper {

      /**
       * Liste des personnes présentes dans les données.
       */
      private List<Person> persons;

      /**
       * Liste des associations adresse-caserne présentes dans les données.
       */
      private List<Firestation> firestations;

      /**
       * Liste des dossiers médicaux présents dans les données.
       */
      private List<MedicalRecord> medicalrecords;
}
