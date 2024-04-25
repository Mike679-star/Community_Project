package com.newcoder.controller;

import com.newcoder.annotation.LoginRequired;
import com.newcoder.pojo.User;
import com.newcoder.service.FollowService;
import com.newcoder.service.LikeService;
import com.newcoder.service.UserService;
import com.newcoder.util.CommunityConstant;
import com.newcoder.util.CommunityUtil;
import com.newcoder.util.HostHolder;
import javax.servlet.http.HttpServletResponse;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${quniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model) {
        // 生成上传文件名称
        String fileName = CommunityUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    // 更新头像的路径
    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空！");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJSONString(0);
    }

    // 废弃
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确！");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户的头像路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    // 废弃
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读取图像失败：" + e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, String confirmNewPassword, Model model, @CookieValue("ticket") String ticket) {
        // 首先对传入的参数进行判断，不能为空
        if (StringUtils.isBlank(oldPassword)) {
            model.addAttribute("oldPasswordMsg", "请输入原始密码！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newPasswordMsg", "请输入新密码！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmNewPassword)) {
            model.addAttribute("confirmNewPasswordMsg", "请输入确认密码！");
            return "/site/setting";
        }

        // 通过该用户进程从hostHolder中获取用户
        User user = hostHolder.getUser();
        // 判断用户输入的原始密码是否与数据库中的一致
        // 对用户输入的原始密码进行加密处理
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "原始密码输入不正确！");
            return "/site/setting";
        }

        // 判断新密码是否与原密码相同
        // 对用户输入的新密码进行加密处理
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        if (newPassword.equals(oldPassword)) {
            model.addAttribute("newPasswordMsg", "新密码与原密码一致！");
            return "/site/setting";
        }

        // 判断确认密码是否与新密码相同
        // 对用户输入的确认密码进行加密处理
        confirmNewPassword = CommunityUtil.md5(confirmNewPassword + user.getSalt());
        if (!confirmNewPassword.equals(newPassword)) {
            model.addAttribute("confirmNewPasswordMsg", "两次密码不一致!");
            return "/site/setting";
        }

        userService.updatePassword(user.getId(), newPassword);
        // 修改密码后，用户需要重新登录
        userService.logout(ticket);
        return "redirect:/login";
    }

    // 个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}
