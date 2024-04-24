package xyz.needpankiller.timber.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Slf4j
//@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Confiig> {


    @Override
    public GatewayFilter apply(Confiig config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("JwtFilter baseMessage>>>>>>" + config.toString() + request.getId());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("JwtFilter response code>>>>>>" + response.getStatusCode());
            }));
        };

    }

    public static class Confiig {
        // Put the configuration properties here
    }
}
