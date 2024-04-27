package xyz.needpankiller.timber.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua_parser.Client;
import ua_parser.Parser;
import xyz.needpankiller.timber.gateway.helper.TimeHelper;
import xyz.needpankiller.timber.gateway.lib.RequestBodyDecorator;
import xyz.needpankiller.timber.gateway.lib.http.AuditLogMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author needpainkiller
 * @version 1.0
 * @description 글로벌 필터 구현
 * @see org.springframework.cloud.gateway.filter.GlobalFilter
 */
@Slf4j
@Component
public class GlobalWebFilter implements GlobalFilter, Ordered {


    @Autowired
    private ObjectMapper objectMapper;

    private static final byte[] EMPTY_REQUEST_BYTES = new byte[0];
    private static final byte[] EMPTY_RESPONSE_BYTES = {};
    private static final String RESPONSE = "response";

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
        return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                    final byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    DataBufferUtils.release(dataBuffer.read(bytes));
                    return bytes;  // 일회성 읽기 만 가능한 리퀘스트 바디 데이터를 미리 읽어서 바이트 배열로 변환
                })
                .defaultIfEmpty(EMPTY_BYTES)
                .doOnNext(bytes -> Mono.fromRunnable(() -> {

                }))

                .flatMap(bytes -> {
                    // RequestBodyDecorator를 사용하여 요청 바디를 캐싱
                    final RequestBodyDecorator decorator = new RequestBodyDecorator(exchange, bytes);
                    // 요청 바디를 캐싱한 RequestBodyDecorator를 사용하여 요청을 변경
                    return chain.filter(exchange.mutate().request(decorator).build());
                });

      /*  return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Global Post Filter executed");
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            HttpStatusCode statusCode = response.getStatusCode();

            HttpMethod httpMethod = request.getMethod();

            String requestURI = request.getURI().toString();
            String userAgent = request.getHeaders().getFirst("user-agent");
            String requestIp = Objects.requireNonNull(request.getRemoteAddress()).getHostString();

            String requestContentType = request.getHeaders().getFirst("Content-Type");
            AtomicReference<String> requestPayload = null;
            if (!Strings.isBlank(requestContentType) && requestContentType.startsWith(CONTENT_TYPE_JSON)) {

                request.getBody().map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                }).subscribe(body -> requestPayload.set(body));

            }
            String responseContentType = response.getHeaders().getFirst("Content-Type");
            String responsePayload;
            if (!Strings.isBlank(responseContentType) && responseContentType.startsWith(CONTENT_TYPE_JSON)) {


//                responsePayload = HttpHelper.getResponsePayload(response);
            } else {
                responsePayload = null;
            }
            String token = request.getHeader(JsonWebTokenProvider.BEARER_TOKEN_HEADER);

        }));*/
    }

    @Override
    public int getOrder() {
        return -1;
    }

    String CONTENT_TYPE_JSON = "application/json";
    Parser uaParser = new Parser();

    private void log(HttpMethod httpMethod, int statusCode,
                     String requestURI, String requestIp, String userAgent,
                     String requestContentType, String requestPayload,
                     String responseContentType, String responsePayload,
                     @Nullable String token) {


        Map<String, Serializable> errorData = new HashMap<>();
        AuditLogMessage auditLogMessage = new AuditLogMessage();
        auditLogMessage.setVisibleYn(true);

        Client client = uaParser.parse(userAgent);
        auditLogMessage.setAgentOs(client.os.family);
        auditLogMessage.setAgentOsVersion(client.os.major);
        auditLogMessage.setAgentBrowser(client.userAgent.family);
        auditLogMessage.setAgentBrowserVersion(client.userAgent.major);
        auditLogMessage.setAgentDevice(client.device.family);

        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode);
        auditLogMessage.setHttpStatus(statusCode);
        auditLogMessage.setHttpMethod(httpMethod);

        try {
            auditLogMessage.setRequestIp(requestIp);
            auditLogMessage.setRequestUri(requestURI);
            auditLogMessage.setRequestContentType(requestContentType);
            if (!Strings.isBlank(requestPayload)) {
                auditLogMessage.setRequestPayLoad(requestPayload);
//                auditLog.setRequestPayLoad(CompressHelper.compressString(requestPayload));
            }
            auditLogMessage.setResponseContentType(responseContentType);
            if (!Strings.isBlank(responsePayload)) {
                auditLogMessage.setResponsePayLoad(responsePayload);
//                auditLog.setResponsePayLoad(CompressHelper.compressString(responsePayload));
            }
            auditLogMessage.setCreatedDate(TimeHelper.now());

            if (series != null && (series.equals(HttpStatus.Series.CLIENT_ERROR) || series.equals(HttpStatus.Series.SERVER_ERROR))) {
                errorData = objectMapper.readValue(responsePayload, Map.class);
            }

        } catch (JsonProcessingException | IllegalArgumentException e) {
            errorData.put("message", e.getMessage());
            errorData.put("code", e.getClass().getName());
        }
        auditLogMessage.setErrorData(errorData);
        auditLogMessage.setToken(token);

        log.info("auditLogMessage : {}", auditLogMessage);
//        template.send("timber__topic-audit-api", auditLogMessage);
    }

}