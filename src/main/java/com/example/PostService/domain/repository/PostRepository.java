package com.example.PostService.domain.repository;

import com.example.PostService.domain.model.Post;
import com.example.PostService.domain.model.PostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, PostId> {
}
