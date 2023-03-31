package com.example.reactiveprogrammingdemo.client;

import com.example.reactiveprogrammingdemo.model.Todo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class BlockingTodoClient {

    public RestTemplate restTemplate;

    public List<Todo> getTodos() {

        ResponseEntity<Todo[]> response = restTemplate.exchange("https://jsonplaceholder.typicode.com/todos", HttpMethod.GET, null, Todo[].class);

        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(response.getBody());
    }
}
