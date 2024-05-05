package xyz.needpankiller.timber.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import xyz.needpankiller.timber.gateway.lib.decorator.RequestBodyDecorator;
import xyz.needpankiller.timber.gateway.lib.decorator.ResponseBodyDecorator;
import xyz.needpankiller.timber.gateway.service.AuditProducer;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author needpainkiller
 * @version 1.0
 * @description 글로벌 필터 구현
 * @see org.springframework.cloud.gateway.filter.GlobalFilter
 */
@Slf4j
@Component
public class GlobalWebFilter implements GlobalFilter, Ordered {
    private static final byte[] EMPTY_REQUEST_BYTES = new byte[0];
    private static final String RESPONSE = "response";

    @Autowired
    private AuditProducer auditProducer;

    /**
     * 글로벌 필터 구현
     *
     * @param exchange
     * @param chain
     * @return Mono<Void>
     * return the response
     * reference: https://velog.io/@aaa6400/Spring-Webflux-%EB%A6%AC%ED%80%98%EC%8A%A4%ED%8A%B8%EB%B0%94%EB%94%94-%EC%BA%90%EC%8B%B1%ED%95%98%EA%B8%B0
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Global Pre Filter executed");

        AtomicReference<byte[]> requestPayload = new AtomicReference<>();

        long startTime = System.currentTimeMillis();

        Flux<DataBuffer> body = exchange.getRequest().getBody();
        return DataBufferUtils.join(body)
                .map(dataBuffer -> {
                    final byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    DataBufferUtils.release(dataBuffer.read(bytes));
                    return bytes;
                }).defaultIfEmpty(EMPTY_REQUEST_BYTES)
                // 요청 바디를 캐싱
                .doOnNext(bytes -> Mono.fromRunnable(() -> requestPayload.set(bytes))
                        //수행시간이 긴 블로킹 작업을 넌 블로킹 작업으로 변경
                        .subscribeOn(Schedulers.boundedElastic()).subscribe())
                .flatMap(bytes -> {
                    // RequestBodyDecorator를 사용하여 요청 바디를 캐싱
                    final RequestBodyDecorator requestBodyDecorator = new RequestBodyDecorator(exchange, bytes);
                    // ResponseBodyDecorator를 사용하여 응답 바디를 캐싱
                    final ResponseBodyDecorator responseBodyDecorator = new ResponseBodyDecorator(exchange);
                    log.info("Global Post Filter executed");
                    return chain.filter(exchange.mutate()
                                    // 요청 바디를 캐싱한 RequestBodyDecorator를 사용하여 요청을 변경
                                    .request(requestBodyDecorator)
                                    // 응답 바디를 캐싱한 ResponseBodyDecorator를 사용하여 응답을 변경
                                    .response(responseBodyDecorator)
                                    .build())
                            .doFinally($ -> Mono.fromRunnable(() -> {
                                final byte[] responseBytes = (byte[]) exchange.getAttributes().remove(RESPONSE);
                                long elapsedTime = System.currentTimeMillis() - startTime;
                                auditProducer.produce(exchange.getRequest(), requestPayload.get(), exchange.getResponse(), responseBytes, elapsedTime).subscribe();

                            }).subscribeOn(Schedulers.boundedElastic()).subscribe());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}