package gov.bnl.eic.nameops.util;

import gov.bnl.eic.nameops.model.NamingElement;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository utility for managing valid naming convention elements.
 * Loads and provides access to valid areas, devices, signals, etc. from configuration.
 */
public class NamingRepositoryUtil {

    private static NamingRepositoryUtil instance;

    private final Map<String, NamingElement> areas;
    private final Map<String, NamingElement> devices;
    private final Map<String, NamingElement> controllers;
    private final Map<String, NamingElement> signals;
    private final Map<String, NamingElement> specificAreas;

    private NamingRepositoryUtil() {
        Map<String, Object> config = loadConfiguration();

        this.areas = loadElements(config, "areas");
        this.devices = loadElements(config, "devices");
        this.controllers = loadElements(config, "controllers");
        this.signals = loadElements(config, "signals");
        this.specificAreas = loadElements(config, "specificAreas");
    }

    /**
     * Get singleton instance
     */
    public static NamingRepositoryUtil getInstance() {
        if (instance == null) {
            synchronized (NamingRepositoryUtil.class) {
                if (instance == null) {
                    instance = new NamingRepositoryUtil();
                }
            }
        }
        return instance;
    }

    /**
     * Load configuration from YAML file
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadConfiguration() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("naming-repository.yaml");

            if (inputStream == null) {
                throw new RuntimeException("naming-repository.yaml not found in resources");
            }

            return yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load naming repository configuration", e);
        }
    }

    /**
     * Load naming elements from configuration
     */
    @SuppressWarnings("unchecked")
    private Map<String, NamingElement> loadElements(Map<String, Object> config, String key) {
        List<Map<String, String>> elements = (List<Map<String, String>>) config.get(key);
        if (elements == null) {
            return new HashMap<>();
        }

        return elements.stream()
            .map(this::mapToNamingElement)
            .collect(Collectors.toMap(
                e -> e.getAbbreviation().toUpperCase(),
                e -> e,
                (e1, e2) -> e1
            ));
    }

    /**
     * Convert map to NamingElement
     */
    private NamingElement mapToNamingElement(Map<String, String> map) {
        NamingElement element = new NamingElement();
        element.setAbbreviation(map.get("abbreviation"));
        element.setFullName(map.get("fullName"));
        element.setDescription(map.get("description"));
        element.setLatticeKeyword(map.get("latticeKeyword"));
        return element;
    }

    /**
     * Check if an area abbreviation is valid
     */
    public boolean isValidArea(String abbreviation) {
        return abbreviation != null && areas.containsKey(abbreviation.toUpperCase());
    }

    /**
     * Get area by abbreviation
     */
    public NamingElement getArea(String abbreviation) {
        if (abbreviation == null) {
            return null;
        }
        return areas.get(abbreviation.toUpperCase());
    }

    /**
     * Get all valid areas
     */
    public Collection<NamingElement> getAllAreas() {
        return areas.values();
    }

    /**
     * Check if a device abbreviation is valid
     */
    public boolean isValidDevice(String abbreviation) {
        return abbreviation != null && devices.containsKey(abbreviation.toUpperCase());
    }

    /**
     * Get device by abbreviation
     */
    public NamingElement getDevice(String abbreviation) {
        if (abbreviation == null) {
            return null;
        }
        return devices.get(abbreviation.toUpperCase());
    }

    /**
     * Get all valid devices
     */
    public Collection<NamingElement> getAllDevices() {
        return devices.values();
    }

    /**
     * Check if a controller abbreviation is valid
     */
    public boolean isValidController(String abbreviation) {
        return abbreviation != null && controllers.containsKey(abbreviation.toUpperCase());
    }

    /**
     * Get controller by abbreviation
     */
    public NamingElement getController(String abbreviation) {
        if (abbreviation == null) {
            return null;
        }
        return controllers.get(abbreviation.toUpperCase());
    }

    /**
     * Get all valid controllers
     */
    public Collection<NamingElement> getAllControllers() {
        return controllers.values();
    }

    /**
     * Check if a signal abbreviation is valid
     */
    public boolean isValidSignal(String abbreviation) {
        return abbreviation != null && signals.containsKey(abbreviation.toUpperCase());
    }

    /**
     * Get signal by abbreviation
     */
    public NamingElement getSignal(String abbreviation) {
        if (abbreviation == null) {
            return null;
        }
        return signals.get(abbreviation.toUpperCase());
    }

    /**
     * Get all valid signals
     */
    public Collection<NamingElement> getAllSignals() {
        return signals.values();
    }

    /**
     * Check if a specific area abbreviation is valid
     */
    public boolean isValidSpecificArea(String abbreviation) {
        return abbreviation != null && specificAreas.containsKey(abbreviation.toUpperCase());
    }

    /**
     * Get specific area by abbreviation
     */
    public NamingElement getSpecificArea(String abbreviation) {
        if (abbreviation == null) {
            return null;
        }
        return specificAreas.get(abbreviation.toUpperCase());
    }

    /**
     * Get all valid specific areas
     */
    public Collection<NamingElement> getAllSpecificAreas() {
        return specificAreas.values();
    }

    /**
     * Get all naming elements as a summary map
     */
    public Map<String, Object> getAllElementsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("areas", getAllAreas());
        summary.put("devices", getAllDevices());
        summary.put("controllers", getAllControllers());
        summary.put("signals", getAllSignals());
        summary.put("specificAreas", getAllSpecificAreas());
        return summary;
    }
}
