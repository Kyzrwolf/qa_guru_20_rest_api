package in.regres.tests;

import in.regres.models.*;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.util.List;

import static in.regres.specs.Specs.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
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

        RegisteredUsersListResponseModel response = step("Make request", () ->
                given(defaultGetRequestSpec)
                        .when()
                        .get("users?page=2")
                        .then()
                        .spec(defaultResponseSpec)
                        .body(matchesJsonSchemaInClasspath("schemas/usersPageSchema.json"))
                        .extract().as(RegisteredUsersListResponseModel.class));

        List<RegisteredUsersListResponseModel.Data> userDataList = response.getData();
        RegisteredUsersListResponseModel.Data tobiasData = userDataList.get(2);

        step("Check response", () -> {
            assertThat(tobiasData.getId(), equalTo(9));
            assertThat(tobiasData.getEmail(), equalTo("tobias.funke@reqres.in"));
            assertThat(tobiasData.getFirstName(), equalTo("Tobias"));
            assertThat(tobiasData.getLastName(), equalTo("Funke"));
            assertThat(tobiasData.getAvatar(), equalTo("https://reqres.in/img/faces/9-image.jpg"));
        });
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    public void createUserTest() {
        CreateUserRequestModel body = new CreateUserRequestModel();
        body.setName("Neo");
        body.setJob("Chosen One");

        CreateUserResponseModel response = step("Make request", () ->
                given(defaultPostRequestSpec)
                        .body(body)
                        .when()
                        .post("users")
                        .then()
                        .spec(createUserResponseSpec)
                        .extract().as(CreateUserResponseModel.class));

        step("Check response", () -> {
            assertThat(response.getName(), equalTo("Neo"));
            assertThat(response.getJob(), equalTo("Chosen One"));
        });
    }

    @Test
    @DisplayName("Проверка возврата кода 404 при обращении за несуществующим пользователем")
    public void userNotFoundTest() {
        step("Make request", () ->
                given(defaultGetRequestSpec)
                        .when()
                        .get("users/23")
                        .then()
                        .spec(notFoundResponseSpec)
                        .log().status()
                        .statusCode(404));
    }

    @Test
    @DisplayName("Проверка успешного логина")
    public void successfulLoginTest() {
        LoginRequestModel body = new LoginRequestModel();
        body.setEmail("eve.holt@reqres.in");
        body.setPassword("cityslicka");

        LoginResponseModel response = step("Make request", () ->
                given(defaultPostRequestSpec)
                        .body(body)
                        .when()
                        .post("login")
                        .then()
                        .spec(defaultResponseSpec)
                        .extract().as(LoginResponseModel.class));

        step("Check response", () -> assertThat(response.getToken(), notNullValue()));
    }

    @Test
    @DisplayName("Проверка обновления данных пользователя")
    public void updateUserTest() {
        CreateUserRequestModel body = new CreateUserRequestModel();
        body.setName("Morpheus");
        body.setJob("some guy");

        UpdateUserResponseModel response = step("Make request", () ->
                given(defaultPostRequestSpec)
                        .body(body)
                    .when()
                        .patch("users/2")
                    .then()
                        .spec(defaultResponseSpec)
                        .extract().as(UpdateUserResponseModel.class));

        step("Check response", () -> {
            assertThat(response.getName(), equalTo("Morpheus"));
            assertThat(response.getJob(), equalTo("some guy"));
            assertThat(response.getUpdatedAt(), notNullValue());
        });
    }
}
