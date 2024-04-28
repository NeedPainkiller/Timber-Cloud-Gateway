package xyz.needpankiller.timber.gateway.lib.decorator;

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
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
    private static final String CONTENT_TYPE_JSON = "application/json";

    private final byte[] bytes;
    private final ServerWebExchange exchange;

    public RequestBodyDecorator(ServerWebExchange exchange, byte[] bytes) {
        super(exchange.getRequest());
        this.bytes = bytes;
        this.exchange = exchange;
    }


    /*
     * 요청 바디를 가져오는 getBody 메소드 오버라이딩
     * exchange 로 부터 요청 헤더를 가져와 Content-Type을 확인하여 요청 바디 캐싱 여부를 결정
     * 요청 바디 캐싱 여부에 따라 요청 바디를 Flux<DataBuffer> 퍼블리셔로 반환
     * @return Flux<DataBuffer>
     * */
    @Override
    public Flux<DataBuffer> getBody() {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        String requestContentType = requestHeaders.getFirst("Content-Type");
        boolean requestBodyCaching = !Strings.isBlank(requestContentType) && requestContentType.startsWith(CONTENT_TYPE_JSON);
        if (bytes == null || bytes.length == 0 || !requestBodyCaching) {
            return super.getBody();
        }
        return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
    }
}
