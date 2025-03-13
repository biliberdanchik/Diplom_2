package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.SetIngredients;
import model.User;

import static io.restassured.RestAssured.given;

public class StellarBurgersServiceClient {
    private final String baseURI;

    public StellarBurgersServiceClient(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Клиент - создание нового пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register")
                .then()
                .log()
                .all();
    }

    @Step("Удаление созданного пользователя")
    public void deleteUser(String accessToken) {
        given()
                .log()
                .all()
                .baseUri(baseURI)
                .auth().oauth2(accessToken)
                .when()
                .delete("/api/auth/user")
                .then()
                .log()
                .all();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(Credentials credentials) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header("Content-Type", "application/json")
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .log()
                .all();
    }

    @Step("Изменение данных пользователя")
    public ValidatableResponse changeUser(String accessToken, User changeDataUser) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .auth().oauth2(accessToken)
                .header("Content-Type", "application/json")
                .body(changeDataUser)
                .when()
                .patch("/api/auth/user")
                .then()
                .log()
                .all();
    }

    @Step("Получение ингредиентов")
    public SetIngredients getIngredients() {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .when()
                .get("/api/ingredients")
                .body().as(SetIngredients.class);
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(String accessToken, String requestBody) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .auth().oauth2(accessToken)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/orders")
                .then()
                .log()
                .all();
    }

    @Step("Получение заказов пользователя")
    public ValidatableResponse getOrdersUser(String accessToken) {
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .auth().oauth2(accessToken)
                .when()
                .get("/api/orders")
                .then()
                .log()
                .all();
    }

    public static String formatToken(String token) {
        return token.replace("Bearer ", "");
    }

    public String createUserAndGetToken(User user) {
        ValidatableResponse creationsResponse = createUser(user);
        return StellarBurgersServiceClient.formatToken(creationsResponse.extract().path("accessToken"));
    }
}
