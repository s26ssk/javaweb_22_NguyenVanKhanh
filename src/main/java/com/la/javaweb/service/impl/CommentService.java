package com.la.javaweb.service.impl;

import com.la.javaweb.dto.request.CommentRequest;
import com.la.javaweb.model.Comment;
import com.la.javaweb.repository.ICommentRepository;
import com.la.javaweb.service.ICommentService;
import com.la.javaweb.util.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class CommentService implements ICommentService {
    @Autowired
    private ICommentRepository commentRepository;

    @Override
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public Page<Comment> getCommentsByProductId(Long productId, Pageable pageable) {
        return commentRepository.findByProductProductId(productId, pageable);
    }

    @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductProductId(productId);
    }

    @Override
    public void saveComment(Comment comment) {

        commentRepository.save(comment);
    }
    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentOfUser(Long commentId) throws AppException{
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found"));

        if (isCommentWithinOneDay(existingComment.getCreatedAt())) {
            commentRepository.deleteById(commentId);
        } else {
            throw new AppException("Cannot delete comment after 1 day");
        }
    }

    @Override
    public Comment updateComment(Long commentId, CommentRequest updateComment) throws AppException {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found"));

        if (isCommentWithinOneDay(existingComment.getCreatedAt())) {
            existingComment.setContent(updateComment.getContent());
            return commentRepository.save(existingComment);
        } else {
            throw new AppException("Cannot update comment after 1 day");
        }
    }
    private boolean isCommentWithinOneDay(Date commentDate) {
        LocalDateTime commentDateTime = commentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        long hoursBetween = ChronoUnit.HOURS.between(commentDateTime, currentDateTime);

        return hoursBetween <= 24;
    }

}
