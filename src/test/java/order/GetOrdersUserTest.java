package order;

import client.StellarBurgersServiceClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.SetIngredients;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrdersUserTest {

    private StellarBurgersServiceClient client;
    private static final String baseURI = "https://stellarburgers.nomoreparties.site";
    private Faker faker;
    private String accessToken;

    @Before
    public void prepareData() {
        client = new StellarBurgersServiceClient(baseURI);
        faker = new Faker();
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void checkingGetOrdersUserWithoutAuthorizationUnsuccessful() {
        ValidatableResponse response = client.getOrdersUser("");
        response.statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void checkingGetOrdersUserWithAuthorizationSuccessful() {
        User user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);

        //Получение списка доступных ингредиентов и формирование тела запроса для создания
        SetIngredients ingredients = client.getIngredients();
        String requestBody = String.format("{\"ingredients\": [\"%s\",\"%s\"]}", ingredients.chooseRandomIngredient(), ingredients.chooseRandomIngredient());
        //Сохраняем id созданного заказа
        String orderId = client.createOrder(accessToken, requestBody).extract().path("order._id");

        //Проверяем, что в теле ответа находится id заказа, который был оформлен
        ValidatableResponse response = client.getOrdersUser(accessToken);
        response.statusCode(200)
                .body("orders[0]._id", equalTo(orderId));
    }

    @After
    public void dataCleanup() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}
