package xyz.needpankiller.timber.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class ProxyConfig {
    public String[] STATIC_RESOURCE_PATHS() {
        return new String[]{
                "/actuator/**",
                "/swagger-ui/**",
                "/swagger.json",
                "/swagger-resources/**",
                "/swagger",
                "/swagger**/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/v2/**",
                "/v3/**",
                "/WEB-INF/**",
                "/web/**",
                "/css/**",
                "/js/**",
                "/img/**",
                "/view/**",
                "/media/**",
                "/static/**",
                "/resources/**",
                "/favicon.ico",
                "/robots.txt",
        };
    }

//    @Autowired
//    private JwtFilter jwtFilter;

    @Bean
    @Profile({"dev", "local"})
    public RouteLocator devRouteLocator(RouteLocatorBuilder builder) {
        log.info("devRouteLocator");
        return builder.routes()
                .route("timber", r -> r.path("/api/**")
//                        .filters(f -> f.filter(jwtFilter.apply(new JwtFilter.Confiig())))
                        .uri("http://localhost:8080"))
                .route("timber", r -> r.path(STATIC_RESOURCE_PATHS())
                        .uri("http://localhost:8080"))
                .build();
    }

    @Bean
    @Profile("prod")
    public RouteLocator prodRouteLocator(RouteLocatorBuilder builder) {
        log.info("prodRouteLocator");
        return builder.routes()
                .route("timber", r -> r.path("/timber/**")
                        .uri("http://timber-application:8080"))
                .build();
    }
}
