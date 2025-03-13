package user;

import client.StellarBurgersServiceClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ChangeUserDataParameterizedTest {

    private final User changeDataUser;

    private StellarBurgersServiceClient client;
    private static final String baseURI = "https://stellarburgers.nomoreparties.site";
    private String accessToken;


    public ChangeUserDataParameterizedTest(User changeDataUser) {
        this.changeDataUser = changeDataUser;
    }

    @Parameterized.Parameters
    public static Object[][] getDataTest() {
        Faker faker = new Faker();
        return new Object[][] {
                //Параметризация с различными наборами данных пользователя
                {new User(faker.internet().emailAddress(), null, null)},
                {new User(null, faker.bothify("#?#?#?#"), null)},
                {new User(null, null, faker.name().firstName())},
                {new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), null)},
                {new User(faker.internet().emailAddress(), null, faker.name().firstName())},
                {new User(null, faker.bothify("#?#?#?#"), faker.name().firstName())}
        };
    }

    @Before
    public void prepareData() {
        client = new StellarBurgersServiceClient(baseURI);
    }

    @Test
    @DisplayName("Изменение данных пользователя с различным набором данных")
    public void checkingChangingVariousUserDataSuccessful() {
        Faker faker = new Faker();
        User user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);

        ValidatableResponse changeResponse = client.changeUser(accessToken, changeDataUser);
        changeResponse.statusCode(200);
    }

    @After
    public void dataCleanup() {
        client.deleteUser(accessToken);
    }

}
