package com.example.reactiveprogrammingdemo.service;

import com.example.reactiveprogrammingdemo.model.Pizza;
import com.example.reactiveprogrammingdemo.repo.PizzaRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class PizzaService {
    private PizzaRepo repo;

    public Flux<Pizza> getPizzas() {
        return repo.getAll();
    }

    public Mono<Pizza> addPizza(Pizza pizza) {

        if (!isValid(pizza)) {
            return Mono.error(new RuntimeException("Invalid Pizza"));
        }
        return repo.findByName(pizza.getName())
                .flatMap(user -> Mono.error(new RuntimeException("Pizza with that name already exists ")))

                .defaultIfEmpty(pizza)
                .cast(Pizza.class)
                .flatMap(it -> repo.save(it));

    }

    private boolean isValid(Pizza pizza) {
        return StringUtils.hasText(pizza.getName());
    }

    public Mono<Pizza> getPizzasById(long id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pizza not found")));
    }

    public Mono<Void> deletePizza(long id) {
        return repo.deleteById(id)
                .then();
    }

/**
 * Example
 */
//    public Mono<LoginDto> createUser(String email, String cellNumber, String password, UserType userType) {
//
//        if (!userType.equals(UserType.CANDIDATE) && !userType.equals(UserType.COMPANY)) {
//            return Mono.error(new UserTypeNotAllowedException(userType + " not allowed"));
//        }
//
//        return userRepository.findByUsername(email)
//                .flatMap(user -> Mono.error(new UserAlreadyExistsException("User with email address already exists " + email)))
//                .defaultIfEmpty(UserDetailsEntity.builder()
//                        .id(encode(UUID.randomUUID()))
//                        .username(email)
//                        .email(email)
//                        .contactNumber(cellNumber)
//                        .userType(userType)
//                        .roles(List.of("ROLE_USER", "ROLE_" + userType))
//                        .dateSignedUp(new Date(clock.getCurrentTimeMillis()))
//                        .password(passwordEncoder.encode(password))
//                        .build())
//                .cast(UserDetailsEntity.class)
//                .doOnNext(emailService::sendWelcomeEmail)
//                .flatMap(this::createAndSaveLoginDetails);
//
//    }
}
