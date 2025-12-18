package com.banquito.branch.holidays.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Branch Holidays API",
                version = "v1",
                description = "API for managing branches and branch holidays"
        )
)
public class OpenApiConfig {
}
