package gov.bnl.eic.nameops.controller;

import gov.bnl.eic.nameops.util.NameGeneratorUtil;
import gov.bnl.eic.nameops.util.NamingRepositoryUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "NameOps", description = "EIC Device Naming Convention API")
public class NameController {

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if the service is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
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
    @Operation(
        summary = "Generate Device Name",
        description = "Generate a standardized device name based on the naming convention (aa:bb-ddpp:zz:nn-cc:ss). " +
                     "Automatically detects whether to use lattice or non-lattice format based on provided parameters."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Name generated successfully",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\":\"success\",\"generatedName\":\"TB:01-PS10\",\"parameters\":{\"area\":\"TB\",\"specificArea\":\"01\",\"device\":\"PS\",\"position\":\"10\"}}"))),
        @ApiResponse(responseCode = "400", description = "Invalid parameters provided")
    })
    public ResponseEntity<Map<String, Object>> generateName(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Device name parameters. Required: area, device. Optional: specificArea, position, secondaryPosition, appendNumber, controller, signal",
                content = @Content(
                    examples = {
                        @ExampleObject(name = "Non-lattice device", value = "{\"area\":\"TB\",\"specificArea\":\"01\",\"device\":\"PS\",\"position\":\"10\"}"),
                        @ExampleObject(name = "Full non-lattice", value = "{\"area\":\"TB\",\"specificArea\":\"01\",\"device\":\"PS\",\"position\":\"10\",\"secondaryPosition\":\"01\",\"appendNumber\":\"01\",\"controller\":\"CC\",\"signal\":\"RB\"}"),
                        @ExampleObject(name = "Lattice device", value = "{\"area\":\"ES\",\"device\":\"Q\",\"position\":\"42\",\"controller\":\"PS\",\"signal\":\"SP\"}")
                    }
                )
            )
            @RequestBody Map<String, String> parameters) {
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
    @Operation(
        summary = "Validate Device Name",
        description = "Validate a device name against EIC naming conventions and repository"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validation result returned",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\":\"success\",\"name\":\"TB:01-PS10\",\"valid\":true,\"message\":\"Name is valid\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public Map<String, Object> validateName(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Request containing the device name to validate",
                content = @Content(
                    examples = @ExampleObject(value = "{\"name\":\"TB:01-PS10:01:01-CC:RB\"}")
                )
            )
            @RequestBody Map<String, String> request) {
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
    @Operation(
        summary = "Get Naming Convention",
        description = "Retrieve the EIC device naming convention syntax and element descriptions"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Convention information returned successfully")
    })
    public Map<String, Object> getConvention() {
        return Map.of(
            "syntax", "aa:bb-ddpp:zz:nn-cc:ss",
            "elements", Map.of(
                "aa", "Area (installation location)",
                "bb", "Specific area within location",
                "dd", "Device function",
                "pp", "Position number",
                "zz", "Secondary position (horizontal/vertical)",
                "nn", "Append number (connection points)",
                "cc", "Controller device",
                "ss", "Signal classification"
            ),
            "description", "EIC device naming convention for non-lattice devices"
        );
    }

    /**
     * Get all valid naming repository elements (areas, devices, signals, etc.)
     */
    @GetMapping("/repository")
    @Operation(
        summary = "Get Naming Repository",
        description = "Retrieve all valid abbreviations and their descriptions from the naming repository"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Repository information returned successfully")
    })
    public Map<String, Object> getRepository() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return repository.getAllElementsSummary();
    }

    /**
     * Get valid areas from the naming repository
     */
    @GetMapping("/repository/areas")
    @Operation(
        summary = "Get Valid Areas",
        description = "Retrieve all valid area abbreviations from the naming repository"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Areas returned successfully")
    })
    public Map<String, Object> getAreas() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("areas", repository.getAllAreas());
    }

    /**
     * Get valid devices from the naming repository
     */
    @GetMapping("/repository/devices")
    @Operation(
        summary = "Get Valid Devices",
        description = "Retrieve all valid device abbreviations from the naming repository"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Devices returned successfully")
    })
    public Map<String, Object> getDevices() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("devices", repository.getAllDevices());
    }

    /**
     * Get valid signals from the naming repository
     */
    @GetMapping("/repository/signals")
    @Operation(
        summary = "Get Valid Signals",
        description = "Retrieve all valid signal abbreviations from the naming repository"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signals returned successfully")
    })
    public Map<String, Object> getSignals() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("signals", repository.getAllSignals());
    }

    /**
     * Get valid controllers from the naming repository
     */
    @GetMapping("/repository/controllers")
    @Operation(
        summary = "Get Valid Controllers",
        description = "Retrieve all valid controller abbreviations from the naming repository"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Controllers returned successfully")
    })
    public Map<String, Object> getControllers() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        return Map.of("controllers", repository.getAllControllers());
    }
}
