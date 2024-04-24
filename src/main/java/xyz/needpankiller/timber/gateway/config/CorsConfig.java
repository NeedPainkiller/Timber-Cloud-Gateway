package xyz.needpankiller.timber.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@Slf4j
public class CorsConfig {
    @Bean
    public GlobalCorsProperties corsProperties() {
        log.info("corsProperties");
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("GET, POST, PUT, DELETE, OPTIONS");

        GlobalCorsProperties globalCorsProperties = new GlobalCorsProperties();
        globalCorsProperties.getCorsConfigurations()
                .put("/**", corsConfiguration);

        return globalCorsProperties;
    }
}