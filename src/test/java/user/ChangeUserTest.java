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

public class ChangeUserTest {
    private StellarBurgersServiceClient client;
    private static final String baseURI = "https://stellarburgers.nomoreparties.site";
    private String accessToken;
    private Faker faker;

    @Before
    public void prepareData() {
        client = new StellarBurgersServiceClient(baseURI);
        faker = new Faker();
    }

    @Test
    @DisplayName("Изменение данных авторизованного пользователя и проверка данных после изменения")
    public void checkingChangesDataAuthorizedUserSuccessful() {
        User user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);

        User changedUser = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        ValidatableResponse changeResponse = client.changeUser(accessToken, changedUser);
        changeResponse.statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(changedUser.getEmail()))
                .body("user.name", equalTo(changedUser.getName()));
    }

    @Test
    @DisplayName("Изменение данных неавторизованного пользователя")
    public void checkingChangesDataUnauthorizedUserUnsuccessful() {
        User changedUser = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        ValidatableResponse changeResponse = client.changeUser("", changedUser);
        changeResponse.statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void dataCleanup() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}
