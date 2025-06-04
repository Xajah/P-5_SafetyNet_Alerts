package com.openclassrooms.P_5_SafetyNet_Alerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application SafetyNet Alerts.
 * <p>
 * Cette classe lance l'application Spring Boot en excluant l'auto-configuration
 * de la source de données JDBC, car l'application utilise un chargement de données
 * depuis un fichier JSON au lieu d'une base de données relationnelle.
 */
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class P5SafetyNetAlertsApplication {

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(P5SafetyNetAlertsApplication.class, args);
    }
}

