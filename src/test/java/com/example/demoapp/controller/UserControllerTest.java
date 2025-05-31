package com.example.demoapp.controller;

import com.example.demoapp.dtos.UserRequest;
import com.example.demoapp.model.User;
import com.example.demoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private User user1;
    private User user2;
    private UserRequest userRequest;

    @BeforeEach
    public void setup() {
        // Create test data
        user1 = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .build();

        user2 = User.builder()
                .id("2")
                .name("Jane Smith")
                .email("jane@example.com")
                .build();

        userRequest = new UserRequest();
        userRequest.setName("New User");
        userRequest.setEmail("newuser@example.com");
    }

    @Test
    public void testGetAllUsers() {
        // Mock service response
        when(userService.getAllUsers()).thenReturn(Flux.just(user1, user2));

        // Test endpoint
        webTestClient.get()
                .uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(2)
                .contains(user1, user2);
    }

    @Test
    public void testGetUserById() {
        // Mock service response
        when(userService.getUserById("1")).thenReturn(Mono.just(user1));

        // Test endpoint
        webTestClient.get()
                .uri("/api/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(user1);
    }

    @Test
    public void testGetUserById_NotFound() {
        // Mock service response for non-existent user
        when(userService.getUserById("999")).thenReturn(Mono.empty());

        // Test endpoint - now expecting 404 Not Found
        webTestClient.get()
                .uri("/api/users/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCreateUser() {
        User newUser = User.builder()
                .id("3")
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();

        // Mock service response
        when(userService.createUser(ArgumentMatchers.any(UserRequest.class))).thenReturn(Mono.just(newUser));

        // Test endpoint
        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .isEqualTo(newUser);
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = user1.toBuilder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();

        // Mock service response
        when(userService.updateUser(anyString(), ArgumentMatchers.any(UserRequest.class))).thenReturn(Mono.just(updatedUser));

        // Test endpoint
        webTestClient.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(updatedUser);
    }

    @Test
    public void testUpdateUser_NotFound() {
        // Mock service response for non-existent user
        when(userService.updateUser(anyString(), ArgumentMatchers.any(UserRequest.class))).thenReturn(Mono.empty());

        // Test endpoint - now expecting 404 Not Found
        webTestClient.put()
                .uri("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testDeleteUser() {
        // Mock service response
        when(userService.deleteUser("1")).thenReturn(Mono.empty());

        // Test endpoint
        webTestClient.delete()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
