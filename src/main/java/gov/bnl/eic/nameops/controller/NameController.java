package gov.bnl.eic.nameops.controller;

import gov.bnl.eic.nameops.util.NameGeneratorUtil;
import gov.bnl.eic.nameops.util.NamingRepositoryUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for device name generation and validation
 */
@RestController
@RequestMapping("/api/v1/nameops")
@CrossOrigin(origins = "*")
public class NameController {

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "service", "NameOps - EIC Device Naming Service",
            "version", "1.0.0"
        );
    }

    /**
     * Generate a device name based on provided parameters
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateName(@RequestBody Map<String, String> parameters) {
        try {
            String generatedName = NameGeneratorUtil.generateDeviceName(parameters);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("generatedName", generatedName);
            response.put("parameters", parameters);
            response.put("message", "Device name generated successfully");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("parameters", parameters);

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            errorResponse.put("parameters", parameters);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Validate a device name against naming conventions
     */
    @PostMapping("/validate")
    public Map<String, Object> validateName(@RequestBody Map<String, String> request) {
        String deviceName = request.get("name");

        // TODO: Implement validation logic
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("name", deviceName);
        response.put("valid", true);
        response.put("message", "Validation endpoint - To be implemented");
        return response;
    }

    /**
     * Get naming convention information
     */
    @GetMapping("/convention")
    public Map<String, Object> getConvention() {
        return Map.of(
            "syntax", "aa:bb-ddpp.zz_nn-ss",
            "elements", Map.of(
                "aa", "Area (installation location)",
                "bb", "Specific area within location",
                "dd", "Device function",
                "pp", "Position number",
                "zz", "Secondary position (horizontal/vertical)",
                "nn", "Append number (connection points)",
                "ss", "Signal classification"
            ),
            "description", "EIC device naming convention for non-lattice devices"
        );
    }

    /**
     * Get all valid naming repository elements (areas, devices, signals, etc.)
     */
    @GetMapping("/repository")
    public Map<String, Object> getRepository() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return repository.getAllElementsSummary();
    }

    /**
     * Get valid areas from the naming repository
     */
    @GetMapping("/repository/areas")
    public Map<String, Object> getAreas() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("areas", repository.getAllAreas());
    }

    /**
     * Get valid devices from the naming repository
     */
    @GetMapping("/repository/devices")
    public Map<String, Object> getDevices() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("devices", repository.getAllDevices());
    }

    /**
     * Get valid signals from the naming repository
     */
    @GetMapping("/repository/signals")
    public Map<String, Object> getSignals() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("signals", repository.getAllSignals());
    }

    /**
     * Get valid controllers from the naming repository
     */
    @GetMapping("/repository/controllers")
    public Map<String, Object> getControllers() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("controllers", repository.getAllControllers());
    }
}
