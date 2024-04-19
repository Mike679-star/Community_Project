package com.newcoder.service;

import com.newcoder.pojo.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int addComment(Comment comment);

}
