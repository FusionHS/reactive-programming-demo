package com.example.reactiveprogrammingdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    private long userId;
    private long id;
    private boolean completed;
    private String title;
}
