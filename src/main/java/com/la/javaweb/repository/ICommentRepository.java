package com.la.javaweb.repository;

import com.la.javaweb.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByProductProductId(Long productId, Pageable pageable);

    List<Comment> findByProductProductId(Long productId);

}
