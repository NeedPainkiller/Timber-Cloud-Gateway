package xyz.needpankiller.timber.gateway.lib;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;


/**
 * RequestBodyDecorator
 *
 * @author needpainkiller
 * @version 1.0
 * @description This class is used to decorate the requset body
 * @reference "https://velog.io/@aaa6400/Spring-Webflux-%EB%A6%AC%ED%80%98%EC%8A%A4%ED%8A%B8%EB%B0%94%EB%94%94-%EC%BA%90%EC%8B%B1%ED%95%98%EA%B8%B0"
 * @see org.springframework.http.server.reactive.ServerHttpRequestDecorator
 */
public class RequestBodyDecorator extends ServerHttpRequestDecorator {

    private final byte[] bytes;
    private final ServerWebExchange exchange;

    public RequestBodyDecorator(ServerWebExchange exchange, byte[] bytes) {
        super(exchange.getRequest());
        this.bytes = bytes;
        this.exchange = exchange;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return bytes == null || bytes.length == 0 ?
                Flux.empty() : Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
    }
}
