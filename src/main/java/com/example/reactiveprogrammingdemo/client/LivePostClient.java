package com.example.reactiveprogrammingdemo.client;

import com.example.reactiveprogrammingdemo.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
@AllArgsConstructor
public class LivePostClient {

    private WebClient webClient;

    public Flux<Post> getLivePosts() {
        return
                webClient.get()
                        .uri("https://jsonplaceholder.typicode.com/posts")
                        .retrieve()
                        .toEntityList(Post.class)
                        .map(HttpEntity::getBody)
                        .flatMapMany(Flux::fromIterable)
                        .delayElements(Duration.ofMillis(2_000));
    }
}
