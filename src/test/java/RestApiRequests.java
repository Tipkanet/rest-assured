import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;

public class RestApiRequests {

    String  loginPattern = "{\"email\": \"%s\",\"password\": \"%s\"}",
            detailsPattern = "{\"name\": \"%s\",\"job\": \"%s\"}";

    @Test
    @DisplayName("Successful getting the single user info with get")    //done
    void successfulGetSingleUserInfoAboutTest() {
        given()
                .get("https://reqres.in/api/users/11")
                .then()
                .statusCode(200)
                .body("data.id", is(11))
                .body("data.email", is("george.edwards@reqres.in"))
                .body("data.first_name", is("George"))
                .body("data.last_name", is("Edwards"))
                .body("support.url", is("https://reqres.in/#support-heading"))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    @DisplayName("Unsuccessful getting the single user info with get. Return 404")  //done
    void unsuccessfulGetSingleUserInfoTest() {
        given()
                .get("https://reqres.in/api/users/404")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Successful login with post")  //done
    void successfulLogin() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format(loginPattern,"eve.holt@reqres.in", "cityslicka"))
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Successful register with post")  //done
    void successfulRegister() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format(loginPattern,"eve.holt@reqres.in", "pistol"))
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .statusCode(200)
                .body("id", is(4))
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Unsuccessful register with post")  //done
    void unsuccessfulRegister() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"eve.holt@reqres.in\"}")
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    @DisplayName("Unsuccessful login with post")    //done
    void unsuccessfulLogin() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"eve.holt@reqres.in\"}")
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    @DisplayName("Successful creating with post")   // done
    void postCreateTest() {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(String.format(detailsPattern, "Tubbo_", "Apiculturist"))
                        .when()
                        .post("https://reqres.in/api/users")
                        .then()
                        .statusCode(201)
                        .extract().response();
        String id = response.path("id").toString();
        System.out.println(id);
        assertThat(response.path("name").toString()).isEqualTo("Tubbo_");
        assertThat(response.path("job").toString()).isEqualTo("Apiculturist");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String  dateHere = formatter.format(new Date()),
                dateFromResponse = response.path("createdAt").toString().substring(0, 10);
        assertThat(dateHere).isEqualTo(dateFromResponse);
    }

    @Test
    @DisplayName("Successful updating with patch")  //done
    void postUpdateWithPatchTest() {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(String.format(detailsPattern, "Tubbo_", "Apiculturist II-degree"))
                        .when()
                        .patch("https://reqres.in/api/users")
                        .then()
                        .statusCode(200)
                        .body("name", is("Tubbo_"))
                        .body("job", is("Apiculturist II-degree"))
                        .extract().response();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String  dateHere = formatter.format(new Date()),
                dateFromResponse = response.path("updatedAt").toString().substring(0, 10);
        assertThat(dateHere).isEqualTo(dateFromResponse);
    }

    @Test
    @DisplayName("Successful updating with put")    //done
    void putUpdateWithPutTest() {
        String response =
        given()
                .contentType(ContentType.JSON)
                .body(String.format(detailsPattern,"Tubbo_", "Nuclear weapon scientist"))
                .when()
                .put("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .body("name", is("Tubbo_"))
                .body("job", is("Nuclear weapon scientist"))
                .extract().path("updatedAt");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = formatter.format(new Date());
        assertThat(date).isEqualTo(response.substring(0, 10));
    }

    @Test
    @DisplayName("Successful delete")   // done
    void deleteTest() {
                given()
                        .contentType(ContentType.JSON)
                        .delete("https://reqres.in/api/users/404")
                        .then()
                        .statusCode(204);
    }
}
