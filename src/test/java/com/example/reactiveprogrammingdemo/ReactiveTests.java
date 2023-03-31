package com.example.reactiveprogrammingdemo;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
public class ReactiveTests {


    @Test
    public void simpleTest() {//Monos are 0..1 elements in a stream


        Mono<String> mono = Mono.just("hi")
                .log()
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNext("hi")
                .verifyComplete();
    }

    @Test
    public void simpleFluxTest() {// Fluxes are 0..infinite elements in a stream


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5)
                .log()
                .doOnNext(it -> System.out.println("Now counting:" + it));

        StepVerifier.create(integerFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    //Basic operators

    @Test
    public void mapFluxTest() {


        Flux<Integer> integerFlux = Flux.range(1, 5)
                .map(it -> it * 2)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(2, 4, 6, 8, 10)
                .verifyComplete();
    }

    @Test
    public void flatMapFluxTest() {


        Flux<Integer> integerFlux = Flux.range(1, 5)
                .flatMap(it -> Mono.just(it * 2))
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(2, 4, 6, 8, 10)
                .verifyComplete();
    }

    @Test
    public void filterFluxTest() {


        Flux<Integer> integerFlux = Flux.range(1, 6)
                .filter(it -> it % 2 == 0)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(2, 4, 6)
                .verifyComplete();
    }

    @Test
    public void distinctFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 2, 2, 2, 6)
                .distinct()
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 6)
                .verifyComplete();
    }

    @Test
    public void takeFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .take(3)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    public void repeatFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .take(3)
                .repeat(2)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 1, 2, 3, 1, 2, 3)
                .verifyComplete();
    }

    @Test
    public void defaultIfEmptyFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .filter(it -> it < 0)
                .defaultIfEmpty(99)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(99)
                .verifyComplete();
    }

    @Test
    public void switchIfEmptyFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .filter(it -> it < 0)
                .switchIfEmpty(Flux.just(99))
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(99)
                .verifyComplete();
    }

    //Side Effects

    @Test
    public void doOnNextFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .doOnNext(it -> System.out.println("I'm counting: " + it))
                .map(it -> it * 10)
                .doOnNext(it -> System.out.println("I'm counting * 10: " + it))
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(10, 20, 30, 40, 50, 60)
                .verifyComplete();
    }

    @Test
    public void doOnNextStreamFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .doOnNext(value -> Mono.just(value)
                                .map(it -> it * 10)
                                .doOnNext(it -> System.out.println("I'm counting * 10: " + it))
//                        .subscribe()
                )
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5, 6)
                .verifyComplete();
    }

    //Error Handling

    @Test
    public void doOnErrorFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .filter(it -> it < 0)
                .switchIfEmpty(Mono.error(new RuntimeException("It's empty")))
                .doOnError(it -> System.out.println("Error side effect: " + it.getMessage()))
                .log();

        StepVerifier.create(integerFlux)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void onErrorReturnFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .filter(it -> it < 0)
                .switchIfEmpty(Mono.error(new RuntimeException("It's empty")))
                .onErrorReturn(99)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(99)
                .verifyComplete();
    }

    @Test
    public void onErrorResumeFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .concatMap(it -> {
                    if (it % 2 == 1) {
                        return Mono.just(it * 10);
                    } else {
                        return Mono.error(new RuntimeException("It's even!"));
                    }
                })
                .onErrorResume(error -> Mono.empty())
//                .onErrorComplete()
//                .onErrorResume(error -> Mono.just(0))
//                .onErrorResume(error -> Mono.error(new RuntimeException("We got an error")))
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    public void onErrorContinueFluxTest() {


        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6)
                .concatMap(it -> {
                    if (it % 2 == 1) {
                        return Mono.just(it * 10);
                    } else {
                        return Mono.error(new RuntimeException("It's even!"));
                    }
                })
                .onErrorContinue(RuntimeException.class, (ex, o) -> System.out.println("Recovered from error: " + ex.getMessage() + ", element:" + o))
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(10, 30, 50)
                .verifyComplete();
    }

    //Combine streams

    @Test
    public void mergeFluxTest() {


        Flux<Integer> integerFlux1 = Flux.just(1, 2, 3);
        Flux<Integer> integerFlux2 = Flux.just(4, 5, 6, 7);
        Flux<Integer> integerFlux = integerFlux1.mergeWith(integerFlux2)
                .log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5, 6, 7)
                .verifyComplete();
    }

    @Test
    public void zipFluxTest() {


        Flux<Integer> integerFlux1 = Flux.just(1, 2, 3, 4);
        Flux<Integer> integerFlux2 = Flux.just(4, 5, 6);
        Flux<Tuple2<Integer, Integer>> tuple2Flux = Flux.zip(integerFlux1, integerFlux2).log();

        StepVerifier.create(tuple2Flux)
                .expectNext(Tuples.of(1, 4), Tuples.of(2, 5), Tuples.of(3, 6))
                .verifyComplete();
    }

    //Parallelism

    @Test
    public void singleFluxTest() {


        var integerFlux = Flux.range(1, 100)
                .map(it -> it * 2)
                .log();

        StepVerifier.create(integerFlux)
                .expectNextCount(100)
                .verifyComplete();
    }

    @Test
    public void parallelFluxTest() {


        var integerFlux = Flux.range(1, 100)
                .parallel(3)
                .runOn(Schedulers.boundedElastic())
                .map(it -> it * 2)
                .log();

        StepVerifier.create(integerFlux)
                .expectNextCount(100)
                .verifyComplete();
    }


    //Pressure

    @Test
    public void coldFluxTest() {

        var integerFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(integerFlux)
                .expectNextCount(10)
                .verifyComplete();

        StepVerifier.create(integerFlux)
                .expectNextCount(10)
                .verifyComplete();

        StepVerifier.create(integerFlux)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    public void hotFluxTest() throws InterruptedException {

        var integerFlux = Flux.range(1, 10)
                .delayElements(Duration.ofMillis(1_000))
                .share()
                .log();

        integerFlux.subscribe(it -> System.out.println("Sub 1 - Counting: " + it));
        Thread.sleep(5_000);
        integerFlux.subscribe(it -> System.out.println("Sub 2 - Counting: " + it));
        Thread.sleep(5_000);
        integerFlux.subscribe(it -> System.out.println("Sub 3 - Counting: " + it));
        Thread.sleep(5_000);

    }

    @Test
    public void hotCacheFluxTest() throws InterruptedException {

        var integerFlux = Flux.range(1, 10)
                .delayElements(Duration.ofMillis(1_000))
                .cache()
                .log();

        integerFlux.subscribe(it -> System.out.println("Sub 1 - Counting: " + it));
        Thread.sleep(5_000);
        integerFlux.subscribe(it -> System.out.println("Sub 2 - Counting: " + it));
        Thread.sleep(5_000);
        integerFlux.subscribe(it -> System.out.println("Sub 3 - Counting: " + it));
        Thread.sleep(5_000);

    }

    @Test
    public void infiniteFluxTest() {
        var integerFlux = Flux.generate(() -> 1, (state, sink) -> {
                    sink.next(state);
                    return state + 1;
                })
                .delayElements(Duration.ofMillis(1_000))
                .log();

        StepVerifier.create(integerFlux.take(10))
                .expectNextCount(10)
                .verifyComplete();

    }

    @Test
    public void infiniteRetryFluxTest() {

        Random random = new Random();

        var integerFlux = Flux.generate(() -> 1, (state, sink) -> {
                    sink.next(state);
                    return state + 1;
                })
                .cast(Integer.class)
                .delayElements(Duration.ofMillis(100))

                .concatMap(it -> {
                    if (random.nextInt() % 2 == 1) {
                        return Mono.just(it);
                    } else {
                        return Mono.error(new RuntimeException("Failed!"));
                    }
                })
                .retry()
                .log();

        StepVerifier.create(integerFlux.take(10))

                .expectNextCount(10)
                .verifyComplete();

    }

    @Test
    public void infiniteRetryIntervalFluxTest() {

        Random random = new Random();
        var counter = new AtomicInteger(0);

        var integerFlux = Flux.interval(Duration.ofMillis(100))
                .map(it -> counter.incrementAndGet())
                .concatMap(it -> {
                    if (random.nextInt() % 2 == 1) {
                        return Mono.just(it);
                    } else {
                        System.out.println("Failed");
                        return Mono.error(new RuntimeException("Failed!"));
                    }
                })
                .retry()
                .log();

        StepVerifier.create(integerFlux.take(10))

                .expectNextCount(10)
                .verifyComplete();

    }

    @Test
    public void infiniteRetryWhenIntervalFluxTest() {

        Random random = new Random();
        var counter = new AtomicInteger(0);

        var integerFlux = Flux.interval(Duration.ofMillis(100))
                .map(it -> counter.incrementAndGet())
                .concatMap(it -> {
                    if (random.nextInt() % 2 == 1) {
                        return Mono.just(it);
                    } else {
                        System.out.println("Failed");
                        return Mono.error(new RuntimeException("Failed!"));
                    }
                })
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .log();

        StepVerifier.create(integerFlux.take(10))
                .expectNextCount(10)
                .verifyComplete();

    }
}
