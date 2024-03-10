package in.regres.tests;


import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@Feature("RestAssured")
@Story("regres.in")
@Tag("API")
public class RegresInTests {

    @BeforeAll
    static void configuration() {
        RestAssured.baseURI = "https://reqres.in/api/";
    }

    @Test
    @DisplayName("Проверка наличия зарегистрированного пользователя Tobias Funke")
    public void userIsRegisteredTest() {
        Response response = given()
                .log().uri()
                .log().method()
        .when()
                .get("users?page=2")
        .then()
                .log().status()
                .log().body(true)
                .body(matchesJsonSchemaInClasspath("schemas/usersPageSchema.json"))
                .statusCode(200)
                .extract().response();

        assertThat(response.path("data[2].id"), equalTo(9));
        assertThat(response.path("data[2].email"), equalTo("tobias.funke@reqres.in"));
        assertThat(response.path("data[2].first_name"), equalTo("Tobias"));
        assertThat(response.path("data[2].last_name"), equalTo("Funke"));
        assertThat(response.path("data[2].avatar"), equalTo("https://reqres.in/img/faces/9-image.jpg"));
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    public void createUserTest() {
        String body = "{ \"name\": \"Neo\", \"job\": \"chosen One\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
        .when()
                .post("users")
        .then()
                .log().status()
                .log().body(true)
                .statusCode(201)
                .extract().response();

        assertThat(response.path("name"), equalTo("Neo"));
        assertThat(response.path("job"), equalTo("chosen One"));
    }

    @Test
    @DisplayName("Проверка возврата кода 404 при обращении за несуществующим пользователем")
    public void userNotFoundTest() {
        given()
                .log().uri()
                .log().method()
                .when()
                .get("users/23")
                .then()
                .log().status()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка успешного логина")
    public void successfulLoginTest() {
        String body = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .post("login")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(200)
                .extract().response();

        assertThat(response.path("token"), notNullValue());
    }

    @Test
    @DisplayName("Проверка обновления данных пользователя")
    public void updateUserTest() {
        String body = "{ \"name\": \"Neo\", \"job\": \"chosen One\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .patch("users/2")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(200)
                .extract().response();

        assertThat(response.path("name"), equalTo("Neo"));
        assertThat(response.path("job"), equalTo("chosen One"));
        assertThat(response.path("updatedAt"), notNullValue());
    }
}
