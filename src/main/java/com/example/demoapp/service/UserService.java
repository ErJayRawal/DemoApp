package com.example.demoapp.service;

import com.example.demoapp.dtos.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demoapp.model.User;
import com.example.demoapp.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> createUser(UserRequest userRequest) {
        return userRepository.save(User.builder().name(userRequest.getName()).email(userRequest.getEmail()).build());
    }

    public Mono<User> updateUser(String id, UserRequest userRequest) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setName(userRequest.getName());
                    existingUser.setEmail(userRequest.getEmail());
                    return userRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }
}
