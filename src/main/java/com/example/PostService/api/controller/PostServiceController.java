package com.example.PostService.api.controller;

import com.example.PostService.api.model.PostInput;
import com.example.PostService.api.model.PostOutput;
import com.example.PostService.api.model.PostSummaryOutput;
import com.example.PostService.api.model.TextProcessingOutput;
import com.example.PostService.common.IdGenerator;
import com.example.PostService.domain.model.Post;
import com.example.PostService.domain.model.PostId;
import com.example.PostService.domain.repository.PostRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostServiceController {

    private static final String FANOUT_EXCHANGE_NAME = "text-processor-service.post-processing.v1.e";
    private final RabbitTemplate rabbitTemplate;

    private final PostRepository postRepository;
    @GetMapping
    public Page<PostSummaryOutput> search(@PageableDefault Pageable pageable){
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(this::convertsToModelSummary);

    }

    @GetMapping("{postId}")
    public PostOutput getDetail(@PathVariable TSID postId){
        Post post = postRepository.findById(new PostId(postId)).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(post);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostOutput createPost(@RequestBody PostInput postInput){
        if(postInput.getAuthor().isBlank() || postInput.getAuthor().isEmpty()|| postInput.getBody().isBlank()|| postInput.getBody().isEmpty() || postInput.getTitle().isBlank()|| postInput.getTitle().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }else{
            Post post = Post.builder()
                    .id(new PostId(IdGenerator.generateTSID()))
                    .author(postInput.getAuthor())
                    .body(postInput.getBody())
                    .title(postInput.getTitle())
                    .build();
            String exchange = FANOUT_EXCHANGE_NAME;
            String routingKey = "";
            Object payload = convertsToModelSummary(post);

            MessagePostProcessor messagePostProcessor = (MessagePostProcessor) message -> {
                message.getMessageProperties().setHeader("PostSummary",payload);
                return message;
            };
            TextProcessingOutput textProcessingOutput =  (TextProcessingOutput) rabbitTemplate.convertSendAndReceive(exchange, routingKey, payload,messagePostProcessor);
            log.info("mensagem enviada para text-processing");
            log.info(textProcessingOutput.toString());
            postRepository.saveAndFlush(post);
            return convertToModel(post);
        }
    }

    private PostOutput convertToModel(Post post) {
        return PostOutput.builder().author(post.getAuthor())
                .body(post.getBody())
                .title(post.getTitle())
                .id(post.getId().toString())
                .summary(post.getSummary())
                .wordCount(post.getWordCount())
                .build();
    }

    private PostSummaryOutput convertsToModelSummary(Post post) {
        return PostSummaryOutput.builder().author(post.getAuthor())
                .body(post.getBody())
                .title(post.getTitle())
                .id(post.getId().toString())
                .build();
    }

}
