package com.example.PostService.api.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInput {
    private String title;
    private String body;
    private String author;
}
