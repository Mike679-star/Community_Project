package com.newcoder.controller.advice;

import com.newcoder.util.CommunityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@ControllerAdvice(annotations = Controller.class)   // 只会扫描带@Controller注解的方法
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }

        String xRequestWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestWith)) {
            // 说明该请求是一个异步请求
            response.setContentType("application/plain;charSet=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
