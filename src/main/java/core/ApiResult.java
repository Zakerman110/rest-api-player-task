package core;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

public class ApiResult<T> {

    private final Response rawResponse;
    private final T body;

    public ApiResult(Response response, TypeRef<T> typeRef) {
        this.rawResponse = response;
        if (typeRef != null && response.getStatusCode() < 400 && response.getContentType() != null) {
            this.body = response.as(typeRef);
        } else {
            this.body = null;
        }
    }

    public Response getRawResponse() {
        return rawResponse;
    }

    public T getBody() {
        return body;
    }
}
