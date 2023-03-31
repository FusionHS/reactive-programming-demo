package com.example.reactiveprogrammingdemo.rest;

import com.example.reactiveprogrammingdemo.model.Pizza;
import com.example.reactiveprogrammingdemo.service.PizzaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/pizzas")
@AllArgsConstructor
public class PizzaController {

    private PizzaService pizzaService;

    @GetMapping
    public Flux<Pizza> getPizzas() {
        return pizzaService.getPizzas();
    }

    @GetMapping("/{id}")
    public Mono<Pizza> getPizzas(@PathVariable long id) {
        return pizzaService.getPizzasById(id);
    }

    @PostMapping
    public Mono<Pizza> addPizza(@RequestBody Pizza pizza) {
        return pizzaService.addPizza(pizza);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePizza(@PathVariable long id) {
        return pizzaService.deletePizza(id);
    }
}
