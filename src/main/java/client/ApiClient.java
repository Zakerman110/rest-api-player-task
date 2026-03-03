package client;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import specification.RequestSpecFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiClient {

    public Response send(
            Method method,
            String endpoint,
            Object body,
            Map<String, Object> pathParams,
            Map<String, Object> queryParams
    ) {

        RequestSpecification requestSpec =
                given().spec(RequestSpecFactory.requestSpec());

        if (pathParams != null) {
            requestSpec.pathParams(pathParams);
        }

        if (queryParams != null) {
            requestSpec.queryParams(queryParams);
        }

        if (body != null) {
            requestSpec.body(body);
        }

        return requestSpec.request(method, endpoint);
    }
}
