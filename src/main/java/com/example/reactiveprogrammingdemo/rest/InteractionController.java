package com.example.reactiveprogrammingdemo.rest;

import com.example.reactiveprogrammingdemo.model.Comment;
import com.example.reactiveprogrammingdemo.model.Post;
import com.example.reactiveprogrammingdemo.model.Todo;
import com.example.reactiveprogrammingdemo.service.InteractionService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/interactions")
@AllArgsConstructor
public class InteractionController {

    private InteractionService interactionService;

    @GetMapping("/comments")
    public Flux<Comment> getComments() {
        return interactionService.getComments();
    }

    @GetMapping(value = "/posts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Post> getPosts() {
        return interactionService.getLivePosts();
    }

    @GetMapping("/todos")
    public Flux<Todo> getTodos() {
        return interactionService.getTodos();
    }
}
