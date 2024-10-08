package com.product.controller;

import com.product.BaseIntegration;
import com.product.model.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Base64;

import static com.product.persist.model.Constant.NEW_PRODUCT_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProductControllerTest extends BaseIntegration {
    @Autowired
    WebTestClient webTestClient;


    @Test
    void register_New_Product_Endpoint(){
        // GIVEN
        var response =
        webTestClient
                .post()
                .uri("/product")
                .header("Authorization", "Basic "+basicAuthentication("test_user", "test_user"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonContent("register-product-payload")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .returnResult().getResponseBody();

        // THEN
        assertNotNull(response);
        assertEquals(NEW_PRODUCT_MESSAGE, response.getMessage());
        log.info("Response: {}", response);
        log.info("Product List {}", productRepository.findAll());

    }

    @Test
    void wrongPayload_WhileRegistering_New_Product_Endpoint(){
        // GIVEN
        var response =
                webTestClient
                        .post()
                        .uri("/product")
                        .header("Authorization", "Basic "+basicAuthentication("test_user", "test_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(jsonContent("wrong-register-product-payload")))
                        .exchange()
                        .expectStatus()
                        .isEqualTo(400)
                        .expectBody(String.class)
                        .returnResult().getResponseBody();

        // THEN
        assertNotNull(response);
        log.info("Error Responses: {}", response);
        assertTrue(response.contains("with 5 error(s)"));
        assertTrue(response.contains("[Field error in object 'productView' on field 'name'"));
        assertTrue(response.contains("[Field error in object 'productView' on field 'price'"));
        assertTrue(response.contains("[Field error in object 'productView' on field 'categoryType'"));
        assertTrue(response.contains("[Field error in object 'productView' on field 'companyOwner'"));
        assertTrue(response.contains(" [Field error in object 'productView' on field 'code'"));
    }


    private String basicAuthentication(String username, String password) {
        return Base64.getEncoder().encodeToString(String.join(":",username,password).getBytes());
    }
}
