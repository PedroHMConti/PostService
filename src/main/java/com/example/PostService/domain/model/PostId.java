package com.example.PostService.domain.model;


import io.hypersistence.tsid.TSID;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor()
@EqualsAndHashCode
public class PostId {

    private TSID value;

    public PostId(TSID value){
        Objects.requireNonNull(value);
        this.value = value;
    }
    public PostId(String value){
        Objects.requireNonNull(value);
        this.value = TSID.from(value);
    }
    public PostId(Long value){
        Objects.requireNonNull(value);
        this.value = TSID.from(value);
    }

}
