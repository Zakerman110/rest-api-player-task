package core;

import client.ApiClient;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Map;

public abstract class BaseService {

    protected final ApiClient client;

    protected BaseService() {
        this.client = new ApiClient();
    }

    protected <T> ApiResult<T> execute(
            Method method,
            String endpoint,
            Object body,
            Map<String, Object> pathParams,
            Map<String, Object> queryParams,
            TypeRef<T> typeRef
    ) {

        Response response = client.send(
                method,
                endpoint,
                body,
                pathParams,
                queryParams
        );

        return new ApiResult<>(response, typeRef);
    }
}
