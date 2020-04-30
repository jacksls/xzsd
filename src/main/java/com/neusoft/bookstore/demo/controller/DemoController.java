package com.neusoft.bookstore.demo.controller;

import com.neusoft.bookstore.demo.service.DemoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: qiulx
 * @Date: 2019/9/17
 */

@Api("demo")
@RequestMapping("/demo")
@Controller
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @ResponseBody
    @GetMapping("/listAccount/{id}")
    public List<Long> listAccount(@PathVariable("id") String id){
        List<Long> longs = demoService.listAccount();
        redisTemplate.opsForValue().set("userId","12345");
        Object userId = redisTemplate.opsForValue().get("userId");
        System.out.println(userId);
        return longs;
    }
}
