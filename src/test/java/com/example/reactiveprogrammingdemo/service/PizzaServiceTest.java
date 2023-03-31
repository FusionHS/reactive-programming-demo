package com.example.reactiveprogrammingdemo.service;


import com.example.reactiveprogrammingdemo.model.Pizza;
import com.example.reactiveprogrammingdemo.repo.PizzaRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {

    @Mock
    private PizzaRepo pizzaRepo;

    @InjectMocks
    private PizzaService pizzaService;

    @Test
    public void getPizza() {

        Pizza pizza = new Pizza(1, "test");
        when(pizzaRepo.getAll()).thenReturn(Flux.fromIterable(List.of(pizza)));

        Flux<Pizza> pizzaFlux = pizzaService.getPizzas();

        StepVerifier.create(pizzaFlux)
                .expectNext(pizza)
                .verifyComplete();
    }


}
