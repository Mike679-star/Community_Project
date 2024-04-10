package com.newcoder.service;

import com.newcoder.pojo.DiscussPost;
import java.util.List;

public interface DiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostRows(int userId);

}
