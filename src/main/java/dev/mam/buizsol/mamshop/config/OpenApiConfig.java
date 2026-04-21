package dev.mam.buizsol.mamshop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${app.keycloak.external-url}")
    private String externalUrl;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Bean
    public OpenAPI customOpenAPI() {
        String authUrl = String.format("%s/realms/%s/protocol/openid-connect/auth", externalUrl, realm);
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", externalUrl, realm);

        return new OpenAPI()
                .info(new Info()
                        .title("Mail and Media Shop API")
                        .version("1.0")
                        .description("API documentation for Mail and Media Shop"))
                .addSecurityItem(new SecurityRequirement().addList("keycloak"))
                .components(new Components()
                        .addSecuritySchemes(
                                "keycloak",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("Keycloak OpenID Connect")
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl(authUrl)
                                                        .tokenUrl(tokenUrl)
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID scope")
                                                                .addString("profile", "Profile scope")
                                                                .addString("email", "Email scope"))))));
    }
}
