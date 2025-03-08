package user;

import client.StellarBurgersServiceClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTest {
    private StellarBurgersServiceClient client;
    private User user;
    private static final String baseURI = "https://stellarburgers.nomoreparties.site";
    private Faker faker;
    private String accessToken;

    @Before
    public void prepareData() {
        client = new StellarBurgersServiceClient(baseURI);
        faker = new Faker();
    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void checkingCreateNewUserSuccessful() {
        user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        ValidatableResponse creationsResponse = client.createUser(user);
        accessToken = StellarBurgersServiceClient.formatToken(creationsResponse.extract().path("accessToken"));

        creationsResponse.statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());


    }

    @Test
    @DisplayName("Создание дублирующего пользователя")
    public void checkingCreateUsersDoubleUnsuccessful() {
        user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);

        ValidatableResponse duplicateResponse = client.createUser(user);
        duplicateResponse.statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @After
    public void dataCleanup() {
        client.deleteUser(accessToken);
    }
}
