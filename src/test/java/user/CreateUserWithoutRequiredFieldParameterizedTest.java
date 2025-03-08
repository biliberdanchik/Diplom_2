package user;

import client.StellarBurgersServiceClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserWithoutRequiredFieldParameterizedTest {

    private final String email;
    private final String password;
    private final String name;
    private StellarBurgersServiceClient client;
    private static final String baseURI = "https://stellarburgers.nomoreparties.site";


    public CreateUserWithoutRequiredFieldParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getDataUser() {
        Faker faker = new Faker();
        return new Object[][] {
                {null, faker.bothify("#??##?#?#?"), faker.name().firstName()},
                {faker.internet().emailAddress(), null, faker.name().firstName()},
                {faker.internet().emailAddress(), faker.bothify("#??##?#?#?"), null}
        };
    }

    @Before
    public void prepareDate() {
        client = new StellarBurgersServiceClient(baseURI);
    }

    @Test
    @DisplayName("Создание нового пользователя без обязательного поля")
    public void checkingCreateUserWithoutRequiredFieldUnsuccessful() {
        User user = new User(email, password, name);
        ValidatableResponse response = client.createUser(user);
        response.statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}