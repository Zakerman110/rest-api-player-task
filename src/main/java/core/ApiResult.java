package core;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

public class ApiResult<T> {

    private final Response rawResponse;
    private final T body;

    public ApiResult(Response response, TypeRef<T> typeRef) {
        this.rawResponse = response;
        this.body = typeRef != null ? response.as(typeRef) : null;
    }

    public Response getRawResponse() {
        return rawResponse;
    }

    public T getBody() {
        return body;
    }
}
