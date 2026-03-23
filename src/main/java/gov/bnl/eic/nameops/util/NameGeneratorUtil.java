package gov.bnl.eic.nameops.util;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for generating EIC device names according to the naming convention.
 *
 * Naming Convention Syntax: aa:bb-ddpp:zz:nn-cc:ss
 *
 * Elements:
 * - aa: Area (installation location)
 * - bb: Specific area within location
 * - dd: Device function
 * - pp: Position number
 * - zz: Secondary position (horizontal/vertical)
 * - nn: Append number (connection points)
 * - cc: Controller device
 * - ss: Signal classification
 */
public class NameGeneratorUtil {

    private static final NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();

    // Validation patterns
    private static final Pattern AREA_PATTERN = Pattern.compile("^[A-Z]{2,4}$");
    private static final Pattern SPECIFIC_AREA_PATTERN = Pattern.compile("^[A-Z0-9]{1,4}$");
    private static final Pattern DEVICE_PATTERN = Pattern.compile("^[A-Z]{1,4}$");
    private static final Pattern POSITION_PATTERN = Pattern.compile("^\\d{1,3}$");
    private static final Pattern SECONDARY_POSITION_PATTERN = Pattern.compile("^\\d{1,2}$");
    private static final Pattern APPEND_NUMBER_PATTERN = Pattern.compile("^\\d{1,2}$");
    private static final Pattern SIGNAL_PATTERN = Pattern.compile("^[A-Z]{2,4}$");

    private NameGeneratorUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate a device name for non-lattice devices based on physical location.
     *
     * Syntax: aa:bb-ddpp:zz:nn-cc:ss
     *
     * @param area Area code (required)
     * @param specificArea Specific area within location (optional)
     * @param device Device function code (required)
     * @param position Position number (optional)
     * @param secondaryPosition Secondary position for horizontal/vertical (optional)
     * @param appendNumber Append number for connection points (optional)
     * @param controller Controller device (optional)
     * @param signal Signal classification (optional)
     * @return Generated device name
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public static String generateNonLatticeDeviceName(
            String area,
            String specificArea,
            String device,
            String position,
            String secondaryPosition,
            String appendNumber,
            String controller,
            String signal) {

        // Validate required fields
        validateRequired(area, "Area");
        validateRequired(device, "Device");

        // Validate field formats
        validateField(area, AREA_PATTERN, "Area");
        validateField(device, DEVICE_PATTERN, "Device");

        // Validate against repository
        if (!repository.isValidArea(area)) {
            throw new IllegalArgumentException("Area '" + area + "' is not in the naming repository");
        }
        if (!repository.isValidDevice(device)) {
            throw new IllegalArgumentException("Device '" + device + "' is not in the naming repository");
        }

        if (specificArea != null && !specificArea.isEmpty()) {
            validateField(specificArea, SPECIFIC_AREA_PATTERN, "Specific Area");
        }
        if (position != null && !position.isEmpty()) {
            validateField(position, POSITION_PATTERN, "Position");
        }
        if (secondaryPosition != null && !secondaryPosition.isEmpty()) {
            validateField(secondaryPosition, SECONDARY_POSITION_PATTERN, "Secondary Position");
        }
        if (appendNumber != null && !appendNumber.isEmpty()) {
            validateField(appendNumber, APPEND_NUMBER_PATTERN, "Append Number");
        }
        if (controller != null && !controller.isEmpty()) {
            validateField(controller, DEVICE_PATTERN, "Controller");
            if (!repository.isValidController(controller)) {
                throw new IllegalArgumentException("Controller '" + controller + "' is not in the naming repository");
            }
        }
        if (signal != null && !signal.isEmpty()) {
            validateField(signal, SIGNAL_PATTERN, "Signal");
            if (!repository.isValidSignal(signal)) {
                throw new IllegalArgumentException("Signal '" + signal + "' is not in the naming repository");
            }
        }

        // Build the name
        StringBuilder name = new StringBuilder();

        // aa:bb-ddpp:zz:nn-cc:ss
        name.append(area.toUpperCase());

        if (specificArea != null && !specificArea.isEmpty()) {
            name.append(":").append(specificArea.toUpperCase());
        }

        name.append("-").append(device.toUpperCase());

        if (position != null && !position.isEmpty()) {
            name.append(position);
        }

        if (secondaryPosition != null && !secondaryPosition.isEmpty()) {
            name.append(":").append(secondaryPosition);
        }

        if (appendNumber != null && !appendNumber.isEmpty()) {
            name.append(":").append(appendNumber);
        }

        if (controller != null && !controller.isEmpty()) {
            name.append("-").append(controller.toUpperCase());
        }

        if (signal != null && !signal.isEmpty()) {
            name.append(":").append(signal.toUpperCase());
        }

        return name.toString();
    }

    /**
     * Generate a device name using a parameter map.
     *
     * @param parameters Map containing device parameters
     * @return Generated device name
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public static String generateNonLatticeDeviceName(Map<String, String> parameters) {
        return generateNonLatticeDeviceName(
            parameters.get("area"),
            parameters.get("specificArea"),
            parameters.get("device"),
            parameters.get("position"),
            parameters.get("secondaryPosition"),
            parameters.get("appendNumber"),
            parameters.get("controller"),
            parameters.get("signal")
        );
    }

    /**
     * Generate a lattice device name for beamline components.
     *
     * Syntax: aa-ddpp_nn-ccss
     *
     * @param area Machine prefix/area (required)
     * @param device Device function code (required)
     * @param position S-coordinate distance in meters (required)
     * @param appendNumber Sequential occurrence within meter section (optional)
     * @param controller Controller device code (optional)
     * @param signal Signal type (optional)
     * @return Generated lattice device name
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public static String generateLatticeDeviceName(
            String area,
            String device,
            String position,
            String appendNumber,
            String controller,
            String signal) {

        // Validate required fields
        validateRequired(area, "Area");
        validateRequired(device, "Device");
        validateRequired(position, "Position");

        // Validate field formats
        validateField(area, AREA_PATTERN, "Area");
        validateField(device, DEVICE_PATTERN, "Device");
        validateField(position, POSITION_PATTERN, "Position");

        // Validate against repository
        if (!repository.isValidArea(area)) {
            throw new IllegalArgumentException("Area '" + area + "' is not in the naming repository");
        }
        if (!repository.isValidDevice(device)) {
            throw new IllegalArgumentException("Device '" + device + "' is not in the naming repository");
        }

        if (appendNumber != null && !appendNumber.isEmpty()) {
            validateField(appendNumber, APPEND_NUMBER_PATTERN, "Append Number");
        }
        if (controller != null && !controller.isEmpty()) {
            validateField(controller, DEVICE_PATTERN, "Controller");
            if (!repository.isValidController(controller)) {
                throw new IllegalArgumentException("Controller '" + controller + "' is not in the naming repository");
            }
        }
        if (signal != null && !signal.isEmpty()) {
            validateField(signal, SIGNAL_PATTERN, "Signal");
            if (!repository.isValidSignal(signal)) {
                throw new IllegalArgumentException("Signal '" + signal + "' is not in the naming repository");
            }
        }

        // Build the name
        StringBuilder name = new StringBuilder();

        // aa-ddpp_nn-ccss
        name.append(area.toUpperCase());
        name.append("-").append(device.toUpperCase());
        name.append(position);

        if (appendNumber != null && !appendNumber.isEmpty()) {
            name.append("_").append(appendNumber);
        }

        if (controller != null && !controller.isEmpty()) {
            name.append("-").append(controller.toUpperCase());
        }

        if (signal != null && !signal.isEmpty()) {
            if (controller == null || controller.isEmpty()) {
                name.append("-");
            }
            name.append(signal.toUpperCase());
        }

        return name.toString();
    }

    /**
     * Generate a lattice device name using a parameter map.
     *
     * @param parameters Map containing device parameters
     * @return Generated lattice device name
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public static String generateLatticeDeviceName(Map<String, String> parameters) {
        return generateLatticeDeviceName(
            parameters.get("area"),
            parameters.get("device"),
            parameters.get("position"),
            parameters.get("appendNumber"),
            parameters.get("controller"),
            parameters.get("signal")
        );
    }

    /**
     * Auto-detect device type and generate appropriate name.
     *
     * @param parameters Map containing device parameters
     * @return Generated device name
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public static String generateDeviceName(Map<String, String> parameters) {
        // Check if it's a lattice device (has controller or explicit lattice flag)
        boolean hasController = parameters.containsKey("controller") &&
                               parameters.get("controller") != null &&
                               !parameters.get("controller").isEmpty();
        boolean hasSpecificArea = parameters.containsKey("specificArea") &&
                                 parameters.get("specificArea") != null &&
                                 !parameters.get("specificArea").isEmpty();
        boolean isLattice = parameters.containsKey("lattice") &&
                           Boolean.parseBoolean(parameters.get("lattice"));

        if (isLattice || hasController) {
            return generateLatticeDeviceName(parameters);
        } else {
            return generateNonLatticeDeviceName(parameters);
        }
    }


    private static void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private static void validateField(String value, Pattern pattern, String fieldName) {
        if (!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " format is invalid: " + value);
        }
    }
}
