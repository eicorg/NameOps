package gov.bnl.eic.nameops.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nameOpsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NameOps - EIC Device Naming Service API")
                        .description("REST API for generating and validating EIC (Electron-Ion Collider) device names according to standardized naming conventions.\n\n" +
                                "## Naming Convention Syntax\n\n" +
                                "The generalized nomenclature syntax is: `aa:bb-ddpp:zz:nn-cc:ss`\n\n" +
                                "### Elements:\n" +
                                "- **aa**: Area (installation location)\n" +
                                "- **bb**: Specific area within location\n" +
                                "- **dd**: Device function\n" +
                                "- **pp**: Position number\n" +
                                "- **zz**: Secondary position (horizontal/vertical)\n" +
                                "- **nn**: Append number (connection points)\n" +
                                "- **cc**: Controller device\n" +
                                "- **ss**: Signal classification\n\n" +
                                "## Features\n" +
                                "- Generate standardized device names\n" +
                                "- Validate names against naming conventions\n" +
                                "- Support for both lattice and non-lattice devices\n" +
                                "- Repository-based validation of abbreviations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BNL EIC")
                                .url("https://www.bnl.gov/eic/"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
