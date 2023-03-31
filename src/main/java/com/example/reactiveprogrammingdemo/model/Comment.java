package com.example.reactiveprogrammingdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private long postId;
    private long id;
    private String name;
    private String email;
    private String body;
}
