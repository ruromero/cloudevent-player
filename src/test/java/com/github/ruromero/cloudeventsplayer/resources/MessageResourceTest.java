package com.github.ruromero.cloudeventsplayer.resources;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.ruromero.cloudeventsplayer.LocalModeProfile;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;

@QuarkusTest
@TestProfile(LocalModeProfile.class)
class MessageResourceTest {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void getAll() {
        given()
                .when().get("/messages")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @SuppressWarnings("rawtypes")
    @Test
    void sendEvent() {
        given().contentType(MediaType.APPLICATION_JSON)
                .body(Json.createObjectBuilder().add("message", "¡Hola, mundo!").build().toString())
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-type", "someType")
                .header("ce-source", "io.cloudevents.examples/player")
                .header("ce-subject", "SUBJ-0001")
                .post()
                .then()
                .statusCode(Response.Status.ACCEPTED.getStatusCode());

        List messages = given()
                .when().get("/messages")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(List.class);

        assertEquals(1, messages.size());
    }

}