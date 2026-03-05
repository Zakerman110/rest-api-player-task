package core;

import client.ApiClient;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class BaseService {

    private static final Logger LOGGER = LogManager.getLogger(BaseService.class);


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

        LOGGER.info("{} {} | response time: {} ms", method, endpoint, response.time());

        return new ApiResult<>(response, typeRef);
    }
}
