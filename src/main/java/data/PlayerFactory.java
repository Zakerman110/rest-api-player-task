package data;

import com.github.javafaker.Faker;
import enums.Gender;
import enums.UserRole;
import models.player.request.CreatePlayerRequest;

public class PlayerFactory {

    private static final Faker faker = new Faker();

    public static CreatePlayerRequest validPlayer() {
        return new CreatePlayerRequest(
                faker.number().numberBetween(16, 60),
                Gender.MALE.getValue(),
                faker.name().username(),
                faker.internet().password(8, 16),
                UserRole.USER.getValue(),
                faker.name().fullName()
        );
    }
}
