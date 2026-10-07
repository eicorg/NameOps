package gov.bnl.eic.nameops.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Utility methods for validating device names against the supported EIC naming syntaxes.
 */
public class NameValidationUtil {

    private NameValidationUtil() {
        // Utility class
    }

    public static ValidationResult validateDeviceName(String deviceName) {
        if (deviceName == null || deviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        String normalizedName = deviceName.trim().toUpperCase(Locale.ROOT);
        List<ValidationCandidate> matches = new ArrayList<>();

        tryValidateNonLattice(normalizedName).ifPresent(matches::add);
        tryValidateLattice(normalizedName).ifPresent(matches::add);

        if (matches.isEmpty()) {
            return new ValidationResult(
                normalizedName,
                false,
                List.of(),
                "Name does not match the EIC lattice or non-lattice naming convention"
            );
        }

        if (matches.size() == 1) {
            ValidationCandidate match = matches.get(0);
            return new ValidationResult(
                normalizedName,
                true,
                List.copyOf(matches),
                "Name is valid for the " + match.format() + " naming convention"
            );
        }

        return new ValidationResult(
            normalizedName,
            true,
            List.copyOf(matches),
            "Name is valid for both lattice and non-lattice naming conventions"
        );
    }

    private static Optional<ValidationCandidate> tryValidateNonLattice(String name) {
        int firstHyphen = name.indexOf('-');
        if (firstHyphen <= 0 || firstHyphen == name.length() - 1 || name.contains("_")) {
            return Optional.empty();
        }

        String locationPart = name.substring(0, firstHyphen);
        String remainder = name.substring(firstHyphen + 1);

        String[] locationSegments = locationPart.split(":", -1);
        if (locationSegments.length < 1 || locationSegments.length > 2 || containsBlankSegment(locationSegments)) {
            return Optional.empty();
        }

        String area = locationSegments[0];
        String specificArea = locationSegments.length == 2 ? locationSegments[1] : null;
        if (specificArea != null && !repository().isValidSpecificArea(specificArea)) {
            return Optional.empty();
        }

        int controllerSeparator = remainder.indexOf('-');
        if (controllerSeparator >= 0 && remainder.indexOf('-', controllerSeparator + 1) >= 0) {
            return Optional.empty();
        }

        String body = controllerSeparator >= 0 ? remainder.substring(0, controllerSeparator) : remainder;
        String controller = null;
        String signalFromTail = null;

        if (controllerSeparator >= 0) {
            String tail = remainder.substring(controllerSeparator + 1);
            String[] tailSegments = tail.split(":", -1);
            if (tailSegments.length < 1 || tailSegments.length > 2 || containsBlankSegment(tailSegments)) {
                return Optional.empty();
            }
            controller = tailSegments[0];
            signalFromTail = tailSegments.length == 2 ? tailSegments[1] : null;
        }

        String[] bodySegments = body.split(":", -1);
        if (bodySegments.length < 1 || bodySegments.length > 4 || containsBlankSegment(bodySegments)) {
            return Optional.empty();
        }

        String deviceAndPosition = bodySegments[0];
        List<String> extras = List.of(bodySegments).subList(1, bodySegments.length);
        List<List<String>> bodyLayouts = getNonLatticeLayouts(controller == null && signalFromTail == null, extras.size());

        for (DevicePosition devicePosition : splitDeviceAndPosition(deviceAndPosition, false)) {
            for (List<String> layout : bodyLayouts) {
                String secondaryPosition = null;
                String appendNumber = null;
                String signal = signalFromTail;

                for (int i = 0; i < layout.size(); i++) {
                    String role = layout.get(i);
                    String value = extras.get(i);
                    if ("secondaryPosition".equals(role)) {
                        secondaryPosition = value;
                    } else if ("appendNumber".equals(role)) {
                        appendNumber = value;
                    } else if ("signal".equals(role)) {
                        signal = value;
                    }
                }

                if (matchesNonLatticeName(
                        name,
                        area,
                        specificArea,
                        devicePosition.device(),
                        devicePosition.position(),
                        secondaryPosition,
                        appendNumber,
                        controller,
                        signal)) {
                    return Optional.of(new ValidationCandidate(
                        "non-lattice",
                        buildComponents(
                            false,
                            area,
                            specificArea,
                            devicePosition.device(),
                            devicePosition.position(),
                            secondaryPosition,
                            appendNumber,
                            controller,
                            signal
                        )
                    ));
                }
            }
        }

        return Optional.empty();
    }

    private static Optional<ValidationCandidate> tryValidateLattice(String name) {
        if (name.contains(":")) {
            return Optional.empty();
        }

        int firstHyphen = name.indexOf('-');
        if (firstHyphen <= 0 || firstHyphen == name.length() - 1) {
            return Optional.empty();
        }

        String area = name.substring(0, firstHyphen);
        String remainder = name.substring(firstHyphen + 1);

        int tailSeparator = remainder.indexOf('-');
        if (tailSeparator >= 0 && remainder.indexOf('-', tailSeparator + 1) >= 0) {
            return Optional.empty();
        }

        String core = tailSeparator >= 0 ? remainder.substring(0, tailSeparator) : remainder;
        String tail = tailSeparator >= 0 ? remainder.substring(tailSeparator + 1) : null;

        String[] coreSegments = core.split("_", -1);
        if (coreSegments.length < 1 || coreSegments.length > 2 || containsBlankSegment(coreSegments)) {
            return Optional.empty();
        }

        String deviceAndPosition = coreSegments[0];
        String appendNumber = coreSegments.length == 2 ? coreSegments[1] : null;

        for (DevicePosition devicePosition : splitDeviceAndPosition(deviceAndPosition, true)) {
            for (ControllerSignal controllerSignal : getLatticeTailCandidates(tail)) {
                if (matchesLatticeName(
                        name,
                        area,
                        devicePosition.device(),
                        devicePosition.position(),
                        appendNumber,
                        controllerSignal.controller(),
                        controllerSignal.signal())) {
                    return Optional.of(new ValidationCandidate(
                        "lattice",
                        buildComponents(
                            true,
                            area,
                            null,
                            devicePosition.device(),
                            devicePosition.position(),
                            null,
                            appendNumber,
                            controllerSignal.controller(),
                            controllerSignal.signal()
                        )
                    ));
                }
            }
        }

        return Optional.empty();
    }

    private static boolean matchesNonLatticeName(
            String expectedName,
            String area,
            String specificArea,
            String device,
            String position,
            String secondaryPosition,
            String appendNumber,
            String controller,
            String signal) {
        try {
            return NameGeneratorUtil.generateNonLatticeDeviceName(
                area,
                specificArea,
                device,
                position,
                secondaryPosition,
                appendNumber,
                controller,
                signal
            ).equals(expectedName);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean matchesLatticeName(
            String expectedName,
            String area,
            String device,
            String position,
            String appendNumber,
            String controller,
            String signal) {
        try {
            return NameGeneratorUtil.generateLatticeDeviceName(
                area,
                device,
                position,
                appendNumber,
                controller,
                signal
            ).equals(expectedName);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static List<List<String>> getNonLatticeLayouts(boolean allowSignalInBody, int extraCount) {
        if (!allowSignalInBody) {
            return switch (extraCount) {
                case 0 -> List.of(List.of());
                case 1 -> List.of(List.of("secondaryPosition"), List.of("appendNumber"));
                case 2 -> List.of(List.of("secondaryPosition", "appendNumber"));
                default -> List.of();
            };
        }

        return switch (extraCount) {
            case 0 -> List.of(List.of());
            case 1 -> List.of(
                List.of("secondaryPosition"),
                List.of("appendNumber"),
                List.of("signal")
            );
            case 2 -> List.of(
                List.of("secondaryPosition", "appendNumber"),
                List.of("secondaryPosition", "signal"),
                List.of("appendNumber", "signal")
            );
            case 3 -> List.of(List.of("secondaryPosition", "appendNumber", "signal"));
            default -> List.of();
        };
    }

    private static List<DevicePosition> splitDeviceAndPosition(String deviceAndPosition, boolean positionRequired) {
        List<DevicePosition> candidates = new ArrayList<>();
        int maxPrefixLength = Math.min(4, deviceAndPosition.length());

        for (int prefixLength = 1; prefixLength <= maxPrefixLength; prefixLength++) {
            String device = deviceAndPosition.substring(0, prefixLength);
            String position = deviceAndPosition.substring(prefixLength);

            if (!repository().isValidDevice(device)) {
                continue;
            }
            if (position.isEmpty() && positionRequired) {
                continue;
            }
            if (!position.isEmpty() && !position.matches("^\\d{1,3}$")) {
                continue;
            }

            candidates.add(new DevicePosition(device, position.isEmpty() ? null : position));
        }

        return candidates;
    }

    private static List<ControllerSignal> getLatticeTailCandidates(String tail) {
        if (tail == null) {
            return List.of(new ControllerSignal(null, null));
        }
        if (tail.isEmpty() || tail.contains(":") || tail.contains("_")) {
            return List.of();
        }

        List<ControllerSignal> candidates = new ArrayList<>();
        candidates.add(new ControllerSignal(tail, null));
        candidates.add(new ControllerSignal(null, tail));

        for (int split = 1; split < tail.length(); split++) {
            candidates.add(new ControllerSignal(
                tail.substring(0, split),
                tail.substring(split)
            ));
        }

        return candidates;
    }

    private static boolean containsBlankSegment(String[] segments) {
        for (String segment : segments) {
            if (segment == null || segment.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> buildComponents(
            boolean lattice,
            String area,
            String specificArea,
            String device,
            String position,
            String secondaryPosition,
            String appendNumber,
            String controller,
            String signal) {
        Map<String, String> components = new LinkedHashMap<>();
        components.put("area", area);
        if (specificArea != null) {
            components.put("specificArea", specificArea);
        }
        components.put("device", device);
        if (position != null) {
            components.put("position", position);
        }
        if (secondaryPosition != null) {
            components.put("secondaryPosition", secondaryPosition);
        }
        if (appendNumber != null) {
            components.put("appendNumber", appendNumber);
        }
        if (controller != null) {
            components.put("controller", controller);
        }
        if (signal != null) {
            components.put("signal", signal);
        }
        components.put("lattice", Boolean.toString(lattice));
        return Collections.unmodifiableMap(components);
    }

    private static NamingRepositoryUtil repository() {
        return NamingRepositoryUtil.getInstance();
    }

    public record ValidationResult(
        String normalizedName,
        boolean valid,
        List<ValidationCandidate> matches,
        String message
    ) {
    }

    public record ValidationCandidate(String format, Map<String, String> components) {
    }

    private record DevicePosition(String device, String position) {
    }

    private record ControllerSignal(String controller, String signal) {
    }
}
