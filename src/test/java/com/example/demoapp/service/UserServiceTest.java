package com.example.demoapp.service;

import com.example.demoapp.dtos.UserRequest;
import com.example.demoapp.model.User;
import com.example.demoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
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
        // Arrange
        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        // Act & Assert
        StepVerifier.create(userService.getAllUsers())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById() {
        // Arrange
        when(userRepository.findById("1")).thenReturn(Mono.just(user1));

        // Act & Assert
        StepVerifier.create(userService.getUserById("1"))
                .expectNext(user1)
                .verifyComplete();

        verify(userRepository, times(1)).findById("1");
    }

    @Test
    public void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userService.getUserById("999"))
                .verifyComplete();

        verify(userRepository, times(1)).findById("999");
    }

    @Test
    public void testGetUserByEmail() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Mono.just(user1));

        // Act & Assert
        StepVerifier.create(userService.getUserByEmail("john@example.com"))
                .expectNext(user1)
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    public void testGetUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userService.getUserByEmail("notfound@example.com"))
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    public void testCreateUser() {
        // Arrange
        User newUser = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
        
        User savedUser = User.builder()
                .id("3")
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
                
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(userService.createUser(userRequest))
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_Found() {
        // Arrange
        User updatedUser = user1.toBuilder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
                
        when(userRepository.findById("1")).thenReturn(Mono.just(user1));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));

        // Act & Assert
        StepVerifier.create(userService.updateUser("1", userRequest))
                .expectNext(updatedUser)
                .verifyComplete();

        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_NotFound() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userService.updateUser("999", userRequest))
                .verifyComplete();

        verify(userRepository, times(1)).findById("999");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        when(userRepository.deleteById("1")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userService.deleteUser("1"))
                .verifyComplete();

        verify(userRepository, times(1)).deleteById("1");
    }
}
