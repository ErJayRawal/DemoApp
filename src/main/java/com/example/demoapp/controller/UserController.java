package com.example.demoapp.controller;

import com.example.demoapp.dtos.UserRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demoapp.model.User;
import com.example.demoapp.service.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Counter userCreationCounter;
    private final Counter userRetrievalCounter;
    private final Timer userCreationTimer;
    private final Timer mongoQueryTimer;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, 
                         @Qualifier("userCreationCounter") Counter userCreationCounter,
                         @Qualifier("userRetrievalCounter") Counter userRetrievalCounter,
                         @Qualifier("userCreationTimer") Timer userCreationTimer,
                         @Qualifier("mongoQueryTimer") Timer mongoQueryTimer) {
        this.userService = userService;
        this.userCreationCounter = userCreationCounter;
        this.userRetrievalCounter = userRetrievalCounter;
        this.userCreationTimer = userCreationTimer;
        this.mongoQueryTimer = mongoQueryTimer;
        
        logger.info("UserController initialized with metrics: userCreationCounter={}, userRetrievalCounter={}, userCreationTimer={}, mongoQueryTimer={}", 
                    userCreationCounter.getId().getName(), 
                    userRetrievalCounter.getId().getName(),
                    userCreationTimer.getId().getName(),
                    mongoQueryTimer.getId().getName());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getAllUsers() {
        logger.info("Retrieving all users");
        Timer.Sample sample = Timer.start();
        
        userRetrievalCounter.increment();
        logger.info("Retrieved all users, incrementing counter: {}, current value should be increased", 
                    userRetrievalCounter.getId().getName());
        
        return mongoQueryTimer.record(() -> userService.getAllUsers())
                .doOnNext(user -> {
                    long duration = sample.stop(mongoQueryTimer);
                    logger.info("Retrieved all users, recorded time: {} ms", 
                                TimeUnit.NANOSECONDS.toMillis(duration));
                });
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUserById(@PathVariable String id) {
        logger.info("Retrieving user by id: {}", id);
        Timer.Sample sample = Timer.start();
        
        userRetrievalCounter.increment();
        logger.info("Retrieved user by id: {}, incrementing counter: {}, current value should be increased", 
                    id, 
                    userRetrievalCounter.getId().getName());
        
        return mongoQueryTimer.record(() -> 
            userService.getUserById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id)))
        ).doOnNext(user -> {
            long duration = sample.stop(mongoQueryTimer);
            logger.info("Retrieved user by id: {}, recorded time: {} ms", 
                        id, 
                        TimeUnit.NANOSECONDS.toMillis(duration));
        });
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody UserRequest userRequest) {
        logger.info("Creating user: {}", userRequest.getName());
        Timer.Sample sample = Timer.start();
        
        userCreationCounter.increment();
        logger.info("User created, incrementing counter: {}, current value should be increased", 
                    userCreationCounter.getId().getName());
        
        return userCreationTimer.record(() -> userService.createUser(userRequest))
                .doOnNext(savedUser -> {
                    long duration = sample.stop(userCreationTimer);
                    logger.info("User created: {}, recorded time: {} ms", 
                                savedUser.getName(), 
                                TimeUnit.NANOSECONDS.toMillis(duration));
                });
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {
        logger.info("Updating user by id: {}", id);
        Timer.Sample sample = Timer.start();
        
        return mongoQueryTimer.record(() -> 
            userService.updateUser(id, userRequest)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id)))
        ).doOnNext(user -> {
            long duration = sample.stop(mongoQueryTimer);
            logger.info("Updated user by id: {}, recorded time: {} ms", 
                        id, 
                        TimeUnit.NANOSECONDS.toMillis(duration));
        });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable String id) {
        logger.info("Deleting user by id: {}", id);
        Timer.Sample sample = Timer.start();
        
        return mongoQueryTimer.record(() -> userService.deleteUser(id))
                .doOnNext(voidValue -> {
                    long duration = sample.stop(mongoQueryTimer);
                    logger.info("Deleted user by id: {}, recorded time: {} ms", 
                                id, 
                                TimeUnit.NANOSECONDS.toMillis(duration));
                });
    }
}
