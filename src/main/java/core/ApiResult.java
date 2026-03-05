package core;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;

public class ApiResult<T> {

    private final Response rawResponse;
    private final T body;

    public ApiResult(Response response, TypeRef<T> typeRef) {
        this.rawResponse = response;

        if (typeRef == null) {
            this.body = null;
            return;
        }

        String contentType = response.getContentType();
        boolean hasContentType = contentType != null && !contentType.isBlank();

        String payload = response.getBody() != null ? response.getBody().asString() : null;
        boolean hasBody = payload != null && !payload.isBlank();

        if (response.getStatusCode() < 400 && hasContentType && hasBody) {
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

    public ApiResult<T> assertStatusCode(int code) {
        rawResponse.then().statusCode(code);
        return this;
    }

    public ApiResult<T> validate(ResponseSpecification spec) {
        rawResponse.then().spec(spec);
        return this;
    }
}
