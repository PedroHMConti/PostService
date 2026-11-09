package com.example.PostService.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostOutput {
    private String id;
    private String title;
    private String body;
    private String author;
    private String summary;
    private Integer wordCount;

}
