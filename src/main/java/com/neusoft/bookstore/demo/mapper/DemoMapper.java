package com.neusoft.bookstore.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DemoMapper {

    List<Long> listAccount();
}
