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
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {

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
    @DisplayName("Создание заказа с авторизацией и наличием ингредиентов")
    public void checkingCreateOrderWithAuthorizationWithIngredientsSuccessful() {
        User user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);

        //Получаем список доступных ингредиентов
        SetIngredients ingredients = client.getIngredients();
        //Формируем тело сообщения с двумя случайными ингредиентами
        String requestBody = String.format("{\"ingredients\": [\"%s\",\"%s\"]}", ingredients.chooseRandomIngredient(), ingredients.chooseRandomIngredient());

        ValidatableResponse creationsOrderResponse = client.createOrder(accessToken, requestBody);
        creationsOrderResponse.statusCode(200)
                .body("success", equalTo(true))
                .body("order._id", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с наличием ингредиентов")
    public void checkingCreateOrderWithoutAuthorizationWithIngredientsSuccessful() {
        //Получаем список доступных ингредиентов
        SetIngredients ingredients = client.getIngredients();
        //Формируем тело сообщения с двумя случайными ингредиентами
        String requestBody = String.format("{\"ingredients\": [\"%s\",\"%s\"]}", ingredients.chooseRandomIngredient(), ingredients.chooseRandomIngredient());

        ValidatableResponse creationsOrderResponse = client.createOrder("", requestBody);
        creationsOrderResponse.statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа c авторизацией без ингредиентов")
    public void checkingCreateOrderWithAuthorizationWithoutIngredientsUnsuccessful() {
        User user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);
        //Тело сообщения без ингредиентов
        String requestBody = "{\"ingredients\": []}";

        ValidatableResponse creationsOrderResponse = client.createOrder(accessToken, requestBody);
        creationsOrderResponse.statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void checkingCreateOrderWithoutAuthorizationWithoutIngredientsUnsuccessful() {
        //Тело сообщения без ингредиентов
        String requestBody = "{\"ingredients\": []}";

        ValidatableResponse creationsOrderResponse = client.createOrder("", requestBody);
        creationsOrderResponse.statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа c неверным хэшем ингредиентов")
    public void checkingCreateOrderWithInvalidHashIngredientsUnsuccessful() {
        User user = new User(faker.internet().emailAddress(), faker.bothify("#?#?#?#"), faker.name().firstName());
        accessToken = client.createUserAndGetToken(user);
        //В тело сообщения генерируем хэш из случайных символов
        String requestBody = String.format("{\"ingredients\": [\"%s\",\"%s\"]}", faker.bothify("##?#?#?##?#?#####?????#?"), faker.bothify("##?#?#?##?#?#####?????#?"));

        ValidatableResponse creationsOrderResponse = client.createOrder(accessToken, requestBody);
        creationsOrderResponse.statusCode(500);
    }

    @After
    public void dataCleanup() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }
}

