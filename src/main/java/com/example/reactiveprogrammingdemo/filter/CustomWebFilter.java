package com.example.reactiveprogrammingdemo.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CustomWebFilter implements WebFilter {

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        //do something before

        return Mono.just(exchange)
                .doOnNext(it -> {
                    //do something before as well
                })

                .flatMap(chain::filter)//do the chain


                .doFinally(signalType -> {
                    //do something after
                });
    }
}
