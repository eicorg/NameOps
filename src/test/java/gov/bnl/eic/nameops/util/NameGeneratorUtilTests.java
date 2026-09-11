package gov.bnl.eic.nameops.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NameGeneratorUtil
 */
class NameGeneratorUtilTests {

    @BeforeAll
    static void setup() {
        // Load test configuration
        NamingRepositoryUtil.setConfigPath("test-naming-repository");
    }

    @AfterAll
    static void tearDown() {
        // Reset to default configuration
        NamingRepositoryUtil.setConfigPath("naming-repository");
    }

    @Test
    void testGenerateNonLatticeDeviceName_AllFields() {
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "TB", "01", "PS", "10", "01", "01", "CC", "RB"
        );
        assertEquals("TB:01-PS10:01:01-CC:RB", result);
    }

    @Test
    void testGenerateNonLatticeDeviceName_MinimalFields() {
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "TB", null, "PS", null, null, null, null, null
        );
        assertEquals("TB-PS", result);
    }

    @Test
    void testGenerateNonLatticeDeviceName_WithoutSpecificArea() {
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "TB", null, "PS", "10", null, null, null, null
        );
        assertEquals("TB-PS10", result);
    }

    @Test
    void testGenerateNonLatticeDeviceName_WithSignal() {
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "TB", "01", "PS", "10", null, null, null, "RB"
        );
        assertEquals("TB:01-PS10:RB", result);
    }

    @Test
    void testGenerateNonLatticeDeviceName_MissingArea() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateNonLatticeDeviceName(
                null, "01", "PS", "10", null, null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Area is required"));
    }

    @Test
    void testGenerateNonLatticeDeviceName_MissingDevice() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateNonLatticeDeviceName(
                "TB", "01", null, "10", null, null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Device is required"));
    }

    @Test
    void testGenerateNonLatticeDeviceName_WithMap() {
        Map<String, String> params = new HashMap<>();
        params.put("area", "TB");
        params.put("specificArea", "01");
        params.put("device", "PS");
        params.put("position", "10");
        params.put("secondaryPosition", "01");
        params.put("appendNumber", "01");
        params.put("controller", "CC");
        params.put("signal", "RB");

        String result = NameGeneratorUtil.generateNonLatticeDeviceName(params);
        assertEquals("TB:01-PS10:01:01-CC:RB", result);
    }

    @Test
    void testGenerateLatticeDeviceName_AllFields() {
        String result = NameGeneratorUtil.generateLatticeDeviceName(
            "ES", "Q", "42", "01", "PS", "RB"
        );
        assertEquals("ES-Q42_01-PSRB", result);
    }

    @Test
    void testGenerateLatticeDeviceName_MinimalFields() {
        String result = NameGeneratorUtil.generateLatticeDeviceName(
            "ES", "Q", "42", null, null, null
        );
        assertEquals("ES-Q42", result);
    }

    @Test
    void testGenerateLatticeDeviceName_WithController() {
        String result = NameGeneratorUtil.generateLatticeDeviceName(
            "ES", "Q", "42", null, "PS", null
        );
        assertEquals("ES-Q42-PS", result);
    }

    @Test
    void testGenerateLatticeDeviceName_WithSignalOnly() {
        String result = NameGeneratorUtil.generateLatticeDeviceName(
            "ES", "Q", "42", null, null, "RB"
        );
        assertEquals("ES-Q42-RB", result);
    }

    @Test
    void testGenerateLatticeDeviceName_WithAppendNumber() {
        String result = NameGeneratorUtil.generateLatticeDeviceName(
            "ES", "Q", "42", "02", null, null
        );
        assertEquals("ES-Q42_02", result);
    }

    @Test
    void testGenerateLatticeDeviceName_MissingArea() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateLatticeDeviceName(
                null, "Q", "42", null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Area is required"));
    }

    @Test
    void testGenerateLatticeDeviceName_MissingDevice() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateLatticeDeviceName(
                "ES", null, "42", null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Device is required"));
    }

    @Test
    void testGenerateLatticeDeviceName_MissingPosition() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateLatticeDeviceName(
                "ES", "Q", null, null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Position is required"));
    }

    @Test
    void testGenerateLatticeDeviceName_WithMap() {
        Map<String, String> params = new HashMap<>();
        params.put("area", "ES");
        params.put("device", "Q");
        params.put("position", "42");
        params.put("controller", "PS");
        params.put("signal", "RB");

        String result = NameGeneratorUtil.generateLatticeDeviceName(params);
        assertEquals("ES-Q42-PSRB", result);
    }

    @Test
    void testGenerateDeviceName_AutoDetectNonLattice() {
        Map<String, String> params = new HashMap<>();
        params.put("area", "TB");
        params.put("specificArea", "01");
        params.put("device", "PS");
        params.put("position", "10");

        String result = NameGeneratorUtil.generateDeviceName(params);
        assertEquals("TB:01-PS10", result);
    }

    @Test
    void testGenerateDeviceName_AutoDetectLattice() {
        Map<String, String> params = new HashMap<>();
        params.put("area", "ES");
        params.put("device", "Q");
        params.put("position", "42");
        params.put("controller", "PS");

        String result = NameGeneratorUtil.generateDeviceName(params);
        assertEquals("ES-Q42-PS", result);
    }

    @Test
    void testGenerateDeviceName_ExplicitLatticeFlag() {
        Map<String, String> params = new HashMap<>();
        params.put("area", "ES");
        params.put("device", "Q");
        params.put("position", "42");
        params.put("lattice", "true");

        String result = NameGeneratorUtil.generateDeviceName(params);
        assertEquals("ES-Q42", result);
    }

    @Test
    void testCaseInsensitive_ConvertToUpperCase() {
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "tb", "01", "ps", "10", null, null, null, "rb"
        );
        assertEquals("TB:01-PS10:RB", result);
    }

    @Test
    void testValidation_InvalidAreaFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateNonLatticeDeviceName(
                "T", null, "PS", "10", null, null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Area format is invalid"));
    }

    @Test
    void testValidation_InvalidDeviceFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            NameGeneratorUtil.generateNonLatticeDeviceName(
                "TB", null, "P", "10", null, null, null, null
            );
        });
        assertTrue(exception.getMessage().contains("Device") &&
                   (exception.getMessage().contains("format is invalid") ||
                    exception.getMessage().contains("not in the naming repository")));
    }

    @Test
    void testComplexExample_NonLattice() {
        // Example: IR:HALL-BPM05:02:03-RDX
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "IR", "HALL", "BPM", "05", "02", "03", null, "RDX"
        );
        assertEquals("IR:HALL-BPM05:02:03:RDX", result);
    }

    @Test
    void testComplexExample_Lattice() {
        // Example: ES6-Q42_01-PSSP
        String result = NameGeneratorUtil.generateLatticeDeviceName(
            "ES6", "Q", "42", "01", "PS", "SP"
        );
        assertEquals("ES6-Q42_01-PSSP", result);
    }

    @Test
    void testEmptyStringFields_TreatedAsNull() {
        String result = NameGeneratorUtil.generateNonLatticeDeviceName(
            "TB", "", "PS", "", "", "", "", ""
        );
        assertEquals("TB-PS", result);
    }
}
