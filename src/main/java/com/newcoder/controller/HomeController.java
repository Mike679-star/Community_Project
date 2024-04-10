package com.newcoder.controller;

import com.newcoder.pojo.DiscussPost;
import com.newcoder.pojo.Page;
import com.newcoder.pojo.Result;
import com.newcoder.pojo.User;
import com.newcoder.service.DiscussPostService;
import com.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @RestController
@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        // 方法调用之前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        // 所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());

        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (list != null) {
            for (DiscussPost post: list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);

        return "/index";
    }

    // @GetMapping("/user/{id}")
    // public Result getIndexPage(@PathVariable Integer id) {
    //     List<DiscussPost> list = discussPostService.findDiscussPosts(id, 0, 10);
    //     for(DiscussPost post : list) {
    //         System.out.println(post);
    //     }
    //     return Result.success(list);
    // }

}
