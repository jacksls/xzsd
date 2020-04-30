package com.neusoft.bookstore.demo.service.impl;

import com.neusoft.bookstore.demo.mapper.DemoMapper;
import com.neusoft.bookstore.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: qiulx
 * @Date: 2019/9/17
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoMapper demoMapper;

    @Override
    public List<Long> listAccount() {
        List<Long> list = demoMapper.listAccount();
        return list;
    }
}
