package com.example.PostService.domain.model;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    @AttributeOverride(name = "value",column = @Column(name = "id", columnDefinition = "bigint"))
    private PostId id;
    private String title;
    private String body;
    private String author;
    private String summary;
    private Integer wordCount;


    private void calcularCamposDerivados() {
        this.summary = createSummary();
        this.wordCount = createWordCount();
    }

    private String createSummary() {
        if (body == null || body.isBlank()) {
            return "";
        }
        return body.lines()
                .limit(3)
                .collect(Collectors.joining("\n"));
    }

    private Integer createWordCount() {
        if (body == null || body.isBlank()) {
            return 0;
        }
        return body.trim().split("\\s+").length;
    }


}
