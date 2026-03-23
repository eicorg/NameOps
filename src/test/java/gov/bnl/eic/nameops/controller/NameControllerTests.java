package gov.bnl.eic.nameops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.bnl.eic.nameops.util.NamingRepositoryUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NameController
 */
@SpringBootTest
class NameControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setupAll() {
        // Load test configuration
        NamingRepositoryUtil.setConfigFileName("test-naming-repository.yaml");
    }

    @AfterAll
    static void tearDownAll() {
        // Reset to default configuration
        NamingRepositoryUtil.setConfigFileName("naming-repository.yaml");
    }

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/nameops/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("NameOps - EIC Device Naming Service"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void testGetConventionEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/nameops/convention"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.syntax").value("aa:bb-ddpp:zz:nn-cc:ss"))
                .andExpect(jsonPath("$.elements.aa").value("Area (installation location)"))
                .andExpect(jsonPath("$.elements.bb").value("Specific area within location"))
                .andExpect(jsonPath("$.elements.dd").value("Device function"))
                .andExpect(jsonPath("$.elements.pp").value("Position number"))
                .andExpect(jsonPath("$.elements.zz").value("Secondary position (horizontal/vertical)"))
                .andExpect(jsonPath("$.elements.nn").value("Append number (connection points)"))
                .andExpect(jsonPath("$.elements.cc").value("Controller device"))
                .andExpect(jsonPath("$.elements.ss").value("Signal classification"))
                .andExpect(jsonPath("$.description").value("EIC device naming convention for non-lattice devices"));
    }

    @Test
    void testGenerateNameEndpoint() throws Exception {
        Map<String, String> parameters = Map.of(
                "area", "TB",
                "specificArea", "01",
                "device", "PS",
                "position", "10"
        );

        mockMvc.perform(post("/api/v1/nameops/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parameters)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.generatedName").value("TB:01-PS10"))
                .andExpect(jsonPath("$.parameters").exists());
    }

    @Test
    void testValidateNameEndpoint() throws Exception {
        Map<String, String> request = Map.of("name", "TB:01-PS10:01:01-CC:RB");

        mockMvc.perform(post("/api/v1/nameops/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.name").value("TB:01-PS10:01:01-CC:RB"))
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void testGenerateNameWithEmptyParameters() throws Exception {
        Map<String, String> parameters = Map.of();

        mockMvc.perform(post("/api/v1/nameops/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parameters)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGenerateNameLatticeDevice() throws Exception {
        Map<String, String> parameters = Map.of(
                "area", "ES",
                "device", "Q",
                "position", "42",
                "controller", "PS",
                "signal", "RB"
        );

        mockMvc.perform(post("/api/v1/nameops/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parameters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.generatedName").value("ES-Q42-PSRB"));
    }

    @Test
    void testGenerateNameComplexNonLattice() throws Exception {
        Map<String, String> parameters = Map.of(
                "area", "TB",
                "specificArea", "01",
                "device", "PS",
                "position", "10",
                "secondaryPosition", "01",
                "appendNumber", "01",
                "signal", "RB"
        );

        mockMvc.perform(post("/api/v1/nameops/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parameters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.generatedName").value("TB:01-PS10.01_01-RB"));
    }

    @Test
    void testValidateNameWithMissingName() throws Exception {
        Map<String, String> request = Map.of();

        mockMvc.perform(post("/api/v1/nameops/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}
