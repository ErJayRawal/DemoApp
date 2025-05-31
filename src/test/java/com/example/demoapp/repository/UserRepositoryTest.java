package com.example.demoapp.repository;

import com.example.demoapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User user2;

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
    }

    @Test
    public void testFindAll() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        // Act & Assert
        StepVerifier.create(userRepository.findAll())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    public void testFindById() {
        // Arrange
        when(userRepository.findById("1")).thenReturn(Mono.just(user1));

        // Act & Assert
        StepVerifier.create(userRepository.findById("1"))
                .expectNext(user1)
                .verifyComplete();
    }

    @Test
    public void testFindById_NotFound() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userRepository.findById("999"))
                .verifyComplete();
    }

    @Test
    public void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Mono.just(user1));

        // Act & Assert
        StepVerifier.create(userRepository.findByEmail("john@example.com"))
                .expectNext(user1)
                .verifyComplete();
    }

    @Test
    public void testFindByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userRepository.findByEmail("notfound@example.com"))
                .verifyComplete();
    }

    @Test
    public void testSave() {
        // Arrange
        User newUser = User.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();
        
        User savedUser = User.builder()
                .id("3")
                .name("New User")
                .email("newuser@example.com")
                .build();
                
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act & Assert
        StepVerifier.create(userRepository.save(newUser))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    public void testDeleteById() {
        // Arrange
        when(userRepository.deleteById(anyString())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userRepository.deleteById("1"))
                .verifyComplete();
    }

    @Test
    public void testDeleteAll() {
        // Arrange
        when(userRepository.deleteAll()).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userRepository.deleteAll())
                .verifyComplete();
    }
}
