package xyz.needpankiller.timber.gateway.lib;

import java.io.Serializable;
import java.util.Arrays;

public enum HttpMethod implements Serializable {
    NONE(0),
    GET(1), HEAD(2), POST(3), PUT(4), PATCH(5), DELETE(6), OPTIONS(7), TRACE(8);

    private final int code;

    HttpMethod(int code) {
        this.code = code;
    }

    public static HttpMethod of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static HttpMethod nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }

    public static boolean hasPayload(HttpMethod httpMethod) {
        return httpMethod.equals(POST) || httpMethod.equals(PUT);
    }
}