package com.example.reactiveprogrammingdemo.repo;

import com.example.reactiveprogrammingdemo.model.Pizza;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PizzaRepo {

    ConcurrentHashMap<Long, Pizza> database = new ConcurrentHashMap<>();
    AtomicInteger counter = new AtomicInteger(0);

    public Flux<Pizza> getAll() {
        return Flux.fromIterable(database.values());
    }

    public Mono<Pizza> save(Pizza pizza) {
        pizza.setId(counter.incrementAndGet());
        database.put(pizza.getId(), pizza);
        return Mono.just(pizza);
    }

    public Mono<Pizza> findById(long id) {
        return Mono.justOrEmpty(database.get(id));

    }

    public Mono<Pizza> deleteById(long id) {
        return Mono.justOrEmpty(database.remove(id));
    }

    public Mono<Pizza> findByName(String name) {
        Optional<Long> optionalPizzaId = getKeyByValue(name);
        return optionalPizzaId.map(id -> Mono.justOrEmpty(database.get(id)))
                .orElseGet(Mono::empty);
    }

    private Optional<Long> getKeyByValue(String value) {
        return database.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue().getName(), value))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
