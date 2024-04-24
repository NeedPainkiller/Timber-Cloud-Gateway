package xyz.needpankiller.timbe.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Global Post Filter executed");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Global Post Filter executed");
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}