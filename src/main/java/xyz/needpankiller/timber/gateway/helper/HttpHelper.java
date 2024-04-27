package xyz.needpankiller.timber.gateway.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class HttpHelper {
/*
    public static String getClientIP(ServerHttpRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static Map<String, String> convertHttpResultToMap(ObjectMapper objectMapper, String source) throws BusinessException {
        try {
            String reformat = source.replace(",", "&").replace(":", "=").replace("\"", "").replace(" ", "").replace("\n", "").replace("}", "").replace("{", "");
            if (objectMapper == null) {
                objectMapper = JacksonConfig.createObjectMapper();
            }
            return objectMapper.readValue(reformat, Map.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(HTTP_RESULT_PARSE_ERROR, e.getMessage());
        }
    }


    public static String getRequestPayload(ServerHttpRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        return getRequestPayload(wrapper);
    }

    public static String getRequestPayload(ContentCachingRequestWrapper wrapper) {
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                //                int length = buf.length;
                return new String(buf, 0, length, StandardCharsets.UTF_8);
            } else {
                return "[empty]";
            }
        } else {
            return "[unknown]";
        }
    }

    public static String getResponsePayload(ServerHttpResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        return getResponsePayload(wrapper);
    }

    public static String getResponsePayload(ContentCachingResponseWrapper wrapper) {
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                //                int length = buf.length;
                return new String(buf, 0, length, StandardCharsets.UTF_8);
            } else {
                return "[empty]";
            }
        } else {
            return "[unknown]";
        }
    }

    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private static final String POST_METHOD = "POST";
    private static final String MULTIPART = "multipart/";

    public static boolean isMultipartContent(
            ServerHttpResponse request) {
        if (!POST_METHOD.equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }*/
}
