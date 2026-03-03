package specification;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecFactory {

    public static ResponseSpecification response200() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    public static ResponseSpecification response201() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .build();
    }
}
