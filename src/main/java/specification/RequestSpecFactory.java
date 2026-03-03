package specification;

import config.ConfigKeys;
import config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;

public class RequestSpecFactory {

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.get(ConfigKeys.BASE_URL))
                .setBasePath(ConfigManager.get(ConfigKeys.BASE_PATH))
                .setContentType(JSON)
                .addHeader("Accept", "application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }
}
