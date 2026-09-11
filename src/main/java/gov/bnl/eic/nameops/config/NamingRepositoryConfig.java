package gov.bnl.eic.nameops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for naming repository settings.
 * This class reads configuration from application.properties and sets system properties
 * that can be accessed by utility classes.
 */
@Configuration
public class NamingRepositoryConfig {

    @Value("${naming.repository.path:${naming.repository.file:naming-repository}}")
    private String repositoryLocation;

    /**
     * Initialize naming repository configuration.
     * This method runs after Spring has injected the properties.
     */
    @PostConstruct
    public void init() {
        System.setProperty("naming.repository.path", repositoryLocation);
        System.out.println("Naming repository location configured: " + repositoryLocation);
    }
}
