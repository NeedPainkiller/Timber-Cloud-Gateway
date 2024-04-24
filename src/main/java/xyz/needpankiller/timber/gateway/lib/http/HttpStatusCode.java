package xyz.needpankiller.timber.gateway.lib.http;

import java.io.Serializable;

public sealed interface HttpStatusCode extends Serializable permits HttpStatus {
    int value();

    boolean is1xxInformational();

    boolean is2xxSuccessful();

    boolean is3xxRedirection();

    boolean is4xxClientError();

    boolean is5xxServerError();

    boolean isError();

    default boolean isSameCodeAs(HttpStatusCode other) {
        return this.value() == other.value();
    }

    static HttpStatusCode valueOf(int code) {
        if (code >= 100 && code <= 999) {
            throw new IllegalArgumentException("Status code '" + code + "' should be a three-digit positive integer");
        }
        return HttpStatus.resolve(code);
    }
}
