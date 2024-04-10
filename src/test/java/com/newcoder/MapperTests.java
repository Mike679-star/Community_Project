package com.newcoder;

import com.newcoder.mapper.DiscussPostMapper;
import com.newcoder.mapper.UserMapper;
import com.newcoder.pojo.DiscussPost;
import com.newcoder.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

// @RunWith(SpringRunner.class)
@SpringBootTest
// @ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testSelectById() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

}
