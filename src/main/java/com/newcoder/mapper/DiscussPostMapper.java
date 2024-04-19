package com.newcoder.mapper;

import com.newcoder.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // @Select("select id from discuss_post")
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用户给参数取别名
    // 如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    @Select("select id, user_id, title, content, type, status, create_time, comment_count, score from discuss_post where id=#{id}")
    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

}
