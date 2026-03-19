package gov.bnl.eic.nameops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for NameOps - EIC Device Naming Service
 *
 * This service provides REST APIs to generate and validate standardized
 * nomenclature for Electron-Ion Collider components.
 */
@SpringBootApplication
public class NameOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NameOpsApplication.class, args);
    }
}
