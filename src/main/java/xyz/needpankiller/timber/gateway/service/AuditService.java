package xyz.needpankiller.timber.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
 import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ua_parser.Client;
import ua_parser.Parser;
import xyz.needpankiller.timber.gateway.helper.TimeHelper;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class AuditService {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String BEARER_TOKEN_HEADER = "X-Authorization";
    private static Parser uaParser = new Parser();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<Object, Object> template;


    public Mono<Void> audit(ServerHttpRequest request, byte[] requestBody, ServerHttpResponse response, byte[] responseBody) {
        return Mono.fromRunnable(() -> {
            HttpMethod httpMethod = request.getMethod();
            HttpStatusCode statusCode = response.getStatusCode();


            String requestURI = request.getURI().toString();
            String userAgent = request.getHeaders().getFirst("user-agent");
            String requestIp = Objects.requireNonNull(request.getRemoteAddress()).getHostString();

            HttpHeaders requestHeaders = request.getHeaders();
            String requestContentType = requestHeaders.getFirst("Content-Type");
            String requestBodyStr = new String(requestBody, StandardCharsets.UTF_8);


            HttpHeaders responseHeaders = response.getHeaders();
            String responseContentType = responseHeaders.getFirst("Content-Type");
            String responseBodyStr = new String(responseBody, StandardCharsets.UTF_8);

            String token = requestHeaders.getFirst(BEARER_TOKEN_HEADER);

            send(httpMethod, Objects.requireNonNull(statusCode).value(), requestURI, requestIp, userAgent, requestContentType, requestHeaders, requestBodyStr, responseContentType, responseHeaders, responseBodyStr, token);
        });
    }

    private void send(HttpMethod httpMethod, int statusCode,
                      String requestURI, String requestIp, String userAgent,
                      String requestContentType, HttpHeaders requestHeaders, String requestPayload,
                      String responseContentType, HttpHeaders responseHeaders, String responsePayload,
                      @Nullable String token) {
        log.info("httpMethod: {}", httpMethod);
        log.info("statusCode: {}", statusCode);
        log.info("requestURI: {}", requestURI);
        log.info("userAgent: {}", userAgent);
        log.info("requestIp: {}", requestIp);
        log.info("Request ContentType: {}", requestContentType);
        log.info("Request Headers: {}", requestHeaders);
        log.info("Request Body: {}", requestPayload);
        log.info("Response ContentType: {}", responseContentType);
        log.info("Response Headers: {}", responseHeaders);
        log.info("Response Body: {}", responsePayload);
        log.info("token: {}", token);


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
        auditLogMessage.setHttpMethod(xyz.needpankiller.timber.gateway.lib.HttpMethod.nameOf(httpMethod.name()));

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

        log.info("auditLogMessage: {}", auditLogMessage);
        template.send("timber__topic-audit-api", auditLogMessage);
    }
}
