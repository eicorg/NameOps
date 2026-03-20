package gov.bnl.eic.nameops.controller;

import org.springframework.web.bind.annotation.*;

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
    public Map<String, Object> generateName(@RequestBody Map<String, String> parameters) {
        // TODO: Implement name generation logic
        return Map.of(
            "status", "success",
            "message", "Name generation endpoint - To be implemented",
            "parameters", parameters
        );
    }

    /**
     * Validate a device name against naming conventions
     */
    @PostMapping("/validate")
    public Map<String, Object> validateName(@RequestBody Map<String, String> request) {
        String deviceName = request.get("name");

        // TODO: Implement validation logic
        return Map.of(
            "status", "success",
            "name", deviceName,
            "valid", true,
            "message", "Validation endpoint - To be implemented"
        );
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
}
