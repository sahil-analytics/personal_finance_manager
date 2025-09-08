package com.financemanager.webapp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Apply CORS configuration to all API endpoints under /api
                        .allowedOrigins(
                                "http://localhost:8080", // Allow requests from where Spring serves static files
                                "http://localhost:5500", // Example: Allow Live Server in VS Code
                                "http://127.0.0.1:5500"  // Example: Allow Live Server in VS Code (alternative IP)
                                // Add the origin of your frontend if deployed separately in production
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specify allowed HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(false) // Set to true if you need cookies/auth headers (requires specific origins, not '*')
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}