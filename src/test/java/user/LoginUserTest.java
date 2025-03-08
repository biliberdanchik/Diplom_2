package user;

import client.StellarBurgersServiceClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {
    private StellarBurgersServiceClient client;
    private User user;
    private Credentials credentials;
    private static final String baseURI = "https://stellarburgers.nomoreparties.site";
    private Faker faker;
    private String accessToken;

    @Before
    public void prepareData() {
        client = new StellarBurgersServiceClient(baseURI);
        faker = new Faker();
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    public void checkingLoginExistingUserSuccessful() {
        user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        credentials = Credentials.fromUser(user);
        accessToken = client.createUserAndGetToken(user);

        ValidatableResponse authorizationResponse = client.loginUser(credentials);
        authorizationResponse.statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с некорректным email")
    public void checkingLoginUserInvalidEmailUnsuccessful() {
        user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        credentials = Credentials.fromUser(user);
        accessToken = client.createUserAndGetToken(user);

        credentials.setEmail(faker.internet().emailAddress());
        ValidatableResponse authorizationResponse = client.loginUser(credentials);
        authorizationResponse.statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с некорректным паролем")
    public void checkingLoginUserInvalidPasswordUnsuccessful() {
        user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        credentials = Credentials.fromUser(user);
        accessToken = client.createUserAndGetToken(user);

        credentials.setPassword(faker.bothify("#?#?#?#"));
        ValidatableResponse authorizationResponse = client.loginUser(credentials);
        authorizationResponse.statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void dataCleanup() {
        client.deleteUser(accessToken);
    }
}
