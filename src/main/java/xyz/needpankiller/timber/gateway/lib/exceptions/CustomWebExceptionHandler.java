package xyz.needpankiller.timber.gateway.lib.exceptions;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @version 1.0
 * @description 에러 핸들러 구현
 * @see org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
 * @see "https://velog.io/@aaa6400/Spring-Webflux-%EB%A6%AC%EC%8A%A4%ED%8F%B0%EC%8A%A4%EB%B0%94%EB%94%94-%EC%BA%90%EC%8B%B1%ED%95%98%EA%B8%B0#%EC%8A%A4%ED%94%84%EB%A7%81-%EB%94%94%ED%8F%B4%ED%8A%B8-%EC%97%90%EB%9F%AC-%EB%A6%AC%EC%8A%A4%ED%8F%B0%EC%8A%A4%EC%97%90-%EB%8C%80%ED%95%9C-%EC%BA%90%EC%8B%B1-%EC%B6%94%EA%B0%80"
 */

@Component
@Order(-2)
public class CustomWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private static final String RESPONSE = "response";

    public CustomWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /*
     * 에러 응답을 렌더링하는 메소드
     * @param request
     * @return Mono<ServerResponse>
     * */
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

//        에러 Attribute Data 를 가져옴
        final Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

        /**
         * 여기서 에러 가공 처리 작업
         *
         * */

//       에러 Attribute Data 를 바이트 배열로 변환하여 exchange에 저장
        final byte[] bytes = errorAttributes.toString().getBytes(StandardCharsets.UTF_8);
        request.attributes().put(RESPONSE, bytes);
        return ServerResponse.ok().bodyValue(errorAttributes);
    }
}
