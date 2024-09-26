package com.la.javaweb.service;

import com.la.javaweb.dto.request.CommentRequest;
import com.la.javaweb.model.Comment;
import com.la.javaweb.util.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {
    Page<Comment> getAllComments(Pageable pageable);
    Page<Comment> getCommentsByProductId(Long productId, Pageable pageable);
    List<Comment> getCommentsByProductId(Long productId);
    void saveComment(Comment comment);
    void deleteComment(Long commentId);
    void deleteCommentOfUser(Long commentId) throws AppException;
    Comment updateComment(Long commentId, CommentRequest updateComment) throws AppException;
}
