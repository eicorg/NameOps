package gov.bnl.eic.nameops.util;

import gov.bnl.eic.nameops.model.NamingElement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NamingRepositoryUtil
 */
class NamingRepositoryUtilTests {

    @BeforeAll
    static void setup() {
        // Load test configuration
        NamingRepositoryUtil.setConfigFileName("test-naming-repository.json");
    }

    @AfterAll
    static void tearDown() {
        // Reset to default configuration
        NamingRepositoryUtil.setConfigFileName("naming-repository.json");
    }

    @Test
    void testGetInstance() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        assertNotNull(repository);
    }

    @Test
    void testLoadAreas() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        Collection<NamingElement> areas = repository.getAllAreas();
        assertNotNull(areas);
        assertFalse(areas.isEmpty());
        assertTrue(areas.size() >= 5); // Should have ES, IS, TB, IR, HALL
    }

    @Test
    void testIsValidArea() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        assertTrue(repository.isValidArea("ES"));
        assertTrue(repository.isValidArea("TB"));
        assertTrue(repository.isValidArea("IR"));
        assertFalse(repository.isValidArea("INVALID"));
    }

    @Test
    void testIsValidDevice() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        assertTrue(repository.isValidDevice("PS"));
        assertTrue(repository.isValidDevice("Q"));
        assertTrue(repository.isValidDevice("B"));
        assertFalse(repository.isValidDevice("INVALID"));
    }

    @Test
    void testIsValidSignal() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        assertTrue(repository.isValidSignal("RB"));
        assertTrue(repository.isValidSignal("SP"));
        assertTrue(repository.isValidSignal("RD"));
        assertFalse(repository.isValidSignal("INVALID"));
    }

    @Test
    void testGetArea() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        NamingElement tb = repository.getArea("TB");
        assertNotNull(tb);
        assertEquals("TB", tb.getAbbreviation());
        assertEquals("Test Beam", tb.getFullName());
    }

    @Test
    void testGetDevice() {
        NamingRepositoryUtil repository = NamingRepositoryUtil.getInstance();
        NamingElement ps = repository.getDevice("PS");
        assertNotNull(ps);
        assertEquals("PS", ps.getAbbreviation());
        assertEquals("Power Supply", ps.getFullName());
    }
}
