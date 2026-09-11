package gov.bnl.eic.nameops.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.bnl.eic.nameops.model.NamingElement;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository utility for managing valid naming convention elements.
 * Loads and provides access to valid areas, devices, signals, etc. from configuration.
 */
public class NamingRepositoryUtil {

    private static final String DEFAULT_CONFIG_LOCATION = "naming-repository";
    private static final Map<String, String> SECTION_FILES = Map.of(
        "areas", "areas.json",
        "devices", "devices.json",
        "controllers", "controllers.json",
        "signals", "signals.json",
        "specificAreas", "specific-areas.json",
        "validation", "validation.json"
    );

    private static NamingRepositoryUtil instance;
    private static String configLocation;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        configLocation = resolveConfigLocation();
    }

    private final Map<String, NamingElement> areas;
    private final Map<String, NamingElement> devices;
    private final Map<String, NamingElement> controllers;
    private final Map<String, NamingElement> signals;
    private final Map<String, NamingElement> specificAreas;

    private NamingRepositoryUtil() {
        Map<String, Object> config = loadConfiguration();

        System.out.println("Config keys: " + config.keySet());

        this.areas = loadElements(config, "areas");
        this.devices = loadElements(config, "devices");
        this.controllers = loadElements(config, "controllers");
        this.signals = loadElements(config, "signals");
        this.specificAreas = loadElements(config, "specificAreas");

        System.out.println("Loaded " + this.areas.size() + " areas");
        System.out.println("Loaded " + this.devices.size() + " devices");
        System.out.println("Loaded " + this.controllers.size() + " controllers");
        System.out.println("Loaded " + this.signals.size() + " signals");
        System.out.println("Loaded " + this.specificAreas.size() + " specificAreas");
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
     * Set configuration file name (for testing purposes)
     */
    public static void setConfigFileName(String fileName) {
        setConfigPath(fileName);
    }

    /**
     * Set configuration path (for testing purposes)
     */
    public static void setConfigPath(String path) {
        configLocation = path;
        resetInstance();
    }

    /**
     * Reset instance (for testing purposes)
     */
    public static void resetInstance() {
        synchronized (NamingRepositoryUtil.class) {
            instance = null;
        }
    }

    /**
     * Load configuration from JSON file
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadConfiguration() {
        try {
            Map<String, Object> config;
            if (configLocation.toLowerCase().endsWith(".json")) {
                config = loadLegacyConfiguration(configLocation);
            } else {
                config = loadSplitConfiguration(configLocation);
            }
            System.out.println("Successfully loaded naming repository configuration from " + configLocation);
            return config;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load naming repository configuration: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load naming repository configuration", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadLegacyConfiguration(String resourceName) throws Exception {
        InputStream inputStream = getRequiredResource(resourceName);
        return objectMapper.readValue(inputStream, Map.class);
    }

    private Map<String, Object> loadSplitConfiguration(String basePath) throws Exception {
        Map<String, Object> config = new HashMap<>();
        String normalizedBasePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;

        for (Map.Entry<String, String> section : SECTION_FILES.entrySet()) {
            String resourcePath = normalizedBasePath + "/" + section.getValue();
            config.put(section.getKey(), readJsonResource(resourcePath));
        }

        return config;
    }

    private Object readJsonResource(String resourcePath) throws Exception {
        InputStream inputStream = getRequiredResource(resourcePath);
        return objectMapper.readValue(inputStream, Object.class);
    }

    private InputStream getRequiredResource(String resourcePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("ERROR: " + resourcePath + " not found in resources");
            System.err.println("Classpath: " + System.getProperty("java.class.path"));
            throw new RuntimeException(resourcePath + " not found in resources");
        }
        return inputStream;
    }

    private static String resolveConfigLocation() {
        String configuredPath = System.getProperty("naming.repository.path",
            System.getenv("NAMING_REPOSITORY_PATH"));
        if (configuredPath == null || configuredPath.isEmpty()) {
            configuredPath = System.getProperty("naming.repository.file",
                System.getenv("NAMING_REPOSITORY_FILE"));
        }
        if (configuredPath == null || configuredPath.isEmpty()) {
            configuredPath = DEFAULT_CONFIG_LOCATION;
        }
        return configuredPath;
    }

    /**
     * Load naming elements from configuration
     */
    @SuppressWarnings("unchecked")
    private Map<String, NamingElement> loadElements(Map<String, Object> config, String key) {
        List<Map<String, String>> elements = (List<Map<String, String>>) config.get(key);
        System.out.println("Loading elements for key: " + key);
        if (elements == null) {
            System.out.println("No elements found for key: " + key);
            return new HashMap<>();
        }
        System.out.println("Found " + elements.size() + " elements for key: " + key);

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
