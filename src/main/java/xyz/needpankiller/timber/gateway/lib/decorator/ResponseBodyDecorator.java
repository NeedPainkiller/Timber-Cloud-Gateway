package xyz.needpankiller.timber.gateway.lib.decorator;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * ResponseBodyDecorator
 *
 * @author needpainkiller
 * @version 1.0
 * @description This class is used to decorate the response body
 * @reference "https://velog.io/@aaa6400/Spring-Webflux-%EB%A6%AC%EC%8A%A4%ED%8F%B0%EC%8A%A4%EB%B0%94%EB%94%94-%EC%BA%90%EC%8B%B1%ED%95%98%EA%B8%B0"
 * @see org.springframework.http.server.reactive.ServerHttpResponseDecorator
 */
@Slf4j
public class ResponseBodyDecorator extends ServerHttpResponseDecorator {
    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final String RESPONSE = "response";
    private final ServerWebExchange exchange;


    public ResponseBodyDecorator(ServerWebExchange exchange) {
        super(exchange.getResponse());
        this.exchange = exchange;
    }

    /*
     * 응답 전 데이터를 가져오는 writeWith 메소드 오버라이딩
     * exchange 로 부터 응답 헤더를 가져와 Content-Type을 확인하여 응답 바디 캐싱 여부를 결정
     * 응답 바디 캐싱 여부에 따라 응답 바디를 Publisher<? extends DataBuffer> 퍼블리셔로 반환
     * @param body
     * @return Mono<Void>
     * */

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        String responseContentType = responseHeaders.getFirst("Content-Type");
        boolean responseBodyCaching = !Strings.isBlank(responseContentType) && responseContentType.startsWith(CONTENT_TYPE_JSON);

        if (!responseBodyCaching) {
            return super.writeWith(body);
        }

        if (body instanceof Flux<? extends DataBuffer> fluxBody) {
            return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        // 응답 버퍼 팩토리에서 데이터 버퍼를 가져와 조인
                        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        // 조인된 새 데이터 버퍼를 읽어서 바이트 배열로 변환
                        byte[] content = new byte[join.readableByteCount()];
                        DataBufferUtils.release(join.read(content));
                        exchange.getAttributes().put(RESPONSE, content);
                        return dataBufferFactory.wrap(content);
                    }))
                    // 에러 발생시 에러가 발생한 Flux 데이터만 로깅
                    .onErrorContinue(throwable -> !throwable.getLocalizedMessage().isBlank(), (e, o) -> log.error("Error: {} with {}", e.getMessage(), o));
        } else if (body instanceof Mono<? extends DataBuffer> monoBody) {
            return super.writeWith(monoBody.map(dataBuffer -> {
                        // 응답 바이트 추출 후 Attribute에 저장
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        DataBufferUtils.release(dataBuffer.read(content));
                        exchange.getAttributes().put(RESPONSE, content);
                        // Caching 후 응답 데이터 반환
                        return exchange.getResponse().bufferFactory().wrap(content);
                    }))
                    // 에러 발생시 빈 Mono 반환
                    .onErrorResume(
                            throwable -> !throwable.getLocalizedMessage().isBlank(),
                            e -> {
                                log.error("Error: {}", e.getMessage());
                                return Mono.empty();
                            });
        } else {
            return super.writeWith(body);
        }
    }
}
