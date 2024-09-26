package com.la.javaweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentResponse {
    private Long commentId;
    private String fullName;
    private String content;
    private Date commentDate;
}
