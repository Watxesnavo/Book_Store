package org.store.structure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    private String devUrl = "http://localhost:8080";

    @Bean
    public OpenAPI myOpenApi() {
        Server server = new Server();
        server.setUrl(devUrl);
        Info info = new Info()
                .title("Book Store Management")
                .version("1.0")
                .description("this api exposes endpoints to manage books");
        return new OpenAPI().info(info).servers(List.of(server));
    }
}
