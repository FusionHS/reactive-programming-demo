package com.example.reactiveprogrammingdemo.service;

import com.example.reactiveprogrammingdemo.client.BlockingTodoClient;
import com.example.reactiveprogrammingdemo.client.LivePostClient;
import com.example.reactiveprogrammingdemo.model.Comment;
import com.example.reactiveprogrammingdemo.model.Post;
import com.example.reactiveprogrammingdemo.model.Todo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@AllArgsConstructor
@Slf4j
public class InteractionService {

    private WebClient webClient;
    private BlockingTodoClient blockingTodoClient;
    private LivePostClient livePostClient;

    public Flux<Comment> getComments() {
        return webClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/comments")
                .retrieve()
                .toEntityList(Comment.class)
                .map(HttpEntity::getBody)
                .flatMapMany(Flux::fromIterable);

    }

    public Flux<Post> getLivePosts() {
        return livePostClient.getLivePosts()
                .doOnNext(post -> log.info("New post: {}", post));
    }

    public Flux<Todo> getTodos() {

        return Mono.fromCallable(() -> blockingTodoClient.getTodos())
                .publishOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);


    }
}
