package com.newcoder.service;

import com.newcoder.pojo.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticsearchService {

    void saveDiscussPost(DiscussPost post);

    void deleteDiscussPost(int id);

    Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit);

}
